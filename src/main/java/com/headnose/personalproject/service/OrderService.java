package com.headnose.personalproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.*;
import com.headnose.personalproject.entity.*;
import com.headnose.personalproject.exception.NotEnoughInventoryException;
import com.headnose.personalproject.repository.OrderDao;
import com.headnose.personalproject.repository.ProductDao;
import com.headnose.personalproject.repository.UserDao;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final UserDao userDao;

    public OrderService(OrderDao orderDao,
                        ProductDao productDao,
                        UserDao userDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.userDao = userDao;
    }


    // PLACE ORDER
    public void placeOrder(PlaceOrderRequest request) {

        log.info("Attempting to place order");

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("Placing order for user: {}", username);

        User user = userDao.findByUsername(username);

        Order order = new Order();
        order.setUser(user);
        order.setPlacedTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productDao.findById(itemRequest.getProductId());

            if (product.getQuantity() < itemRequest.getQuantity()) {
                log.warn("Insufficient inventory for product {}. Requested: {}, Available: {}",
                        product.getId(),
                        itemRequest.getQuantity(),
                        product.getQuantity());

                throw new NotEnoughInventoryException(
                        String.format(ApiMessages.NOT_ENOUGH_INVENTORY_FORMAT, product.getId()));
            }

            product.setQuantity(
                    product.getQuantity() - itemRequest.getQuantity()
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

            orderItem.setPurchasedRetailPrice(product.getRetailPrice());
            orderItem.setPurchasedWholesalePrice(product.getWholesalePrice());

            order.addOrderItem(orderItem);
        }

        orderDao.save(order);

        log.info("Order successfully placed for user: {}", username);
    }


    // USER CANCEL ORDER
    public void cancelOrderByUser(Long orderId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} attempting to cancel order {}", username, orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Cancel failed: Order {} not found", orderId);
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (!order.getUser().getUsername().equals(username)) {
            log.warn("Cancel failed: User {} does not own order {}", username, orderId);
            throw new RuntimeException(ApiMessages.ORDER_ACCESS_DENIED);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Cancel failed: Order {} is not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_CANCEL);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELED);

        log.info("Order {} successfully canceled by user", orderId);
    }


    // ADMIN COMPLETE ORDER
    public void completeOrderByAdmin(Long orderId) {

        log.info("Admin attempting to complete order {}", orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Admin completion failed: Order {} not found", orderId);
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Admin completion failed: Order {} not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_COMPLETE);
        }

        order.setStatus(OrderStatus.COMPLETED);

        log.info("Order {} marked as COMPLETED by admin", orderId);
    }

    // ADMIN CANCEL ORDER
    public void cancelOrderByAdmin(Long orderId) {

        log.info("Admin attempting to cancel order {}", orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Admin cancel failed: Order {} not found", orderId);
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Admin cancel failed: Order {} not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_CANCEL);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELED);

        log.info("Order {} canceled by admin", orderId);
    }


    // USER VIEW ORDERS
    public List<OrderResponse> getOrdersForCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("Fetching orders for user {}", username);

        User user = userDao.findByUsername(username);

        List<Order> orders = orderDao.findByUser(user);

        log.info("Found {} orders for user {}", orders.size(), username);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByIdForCurrentUser(Long orderId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("User {} attempting to view order {}", username, orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Order {} not found for user {}", orderId, username);
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (!order.getUser().getUsername().equals(username)) {
            log.warn("Access denied for user {} on order {}", username, orderId);
            throw new RuntimeException(ApiMessages.ORDER_ACCESS_DENIED);
        }

        return mapToOrderResponse(order);
    }

    /** Most frequently purchased products for current user (by total quantity), excluding canceled orders. */
    public List<RecentPurchaseItemResponse> getMostFrequentlyPurchasedProducts(int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findByUsername(username);
        List<Order> orders = orderDao.findByUser(user).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELED)
                .collect(Collectors.toList());

        return orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Comparator.<java.util.Map.Entry<Product, Integer>>comparingInt(java.util.Map.Entry::getValue).reversed()
                        .thenComparing(entry -> entry.getKey().getId()))
                .limit(limit)
                .map(entry -> {
                    Product p = entry.getKey();
                    return new RecentPurchaseItemResponse(
                            p.getId(),
                            p.getName() != null ? p.getName() : p.getDescription(),
                            p.getDescription(),
                            p.getRetailPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    /** Most recently purchased order items for current user (excluding canceled orders). */
    public List<RecentPurchaseItemResponse> getMostRecentlyPurchasedItems(int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userDao.findByUsername(username);
        List<Order> orders = orderDao.findByUser(user).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELED)
                .sorted(Comparator.comparing(Order::getPlacedTime).reversed())
                .collect(Collectors.toList());

        return orders.stream()
                .flatMap(o -> o.getOrderItems().stream()
                        .map(item -> new java.util.AbstractMap.SimpleEntry<>(item, o.getPlacedTime())))
                .sorted(Comparator.comparing(java.util.Map.Entry<OrderItem, LocalDateTime>::getValue).reversed())
                .limit(limit)
                .map(e -> {
                    OrderItem item = e.getKey();
                    Product p = item.getProduct();
                    return new RecentPurchaseItemResponse(
                            p.getId(),
                            p.getName() != null ? p.getName() : p.getDescription(),
                            p.getDescription(),
                            item.getPurchasedRetailPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    // ADMIN VIEW ALL ORDERS
    public List<AdminOrderResponse> getAllOrdersForAdmin() {

        log.info("Admin fetching all orders");

        List<Order> orders = orderDao.findAll();

        log.info("Admin retrieved {} total orders", orders.size());

        return orders.stream()
                .map(this::mapToAdminOrderResponse)
                .collect(Collectors.toList());
    }

    // ADMIN GET ORDER BY ID (any order, no user check)
    public AdminOrderResponse getOrderByIdForAdmin(Long orderId) {

        log.info("Admin fetching order {}", orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Order {} not found for admin", orderId);
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        return mapToAdminOrderResponse(order);
    }

    /** Top 3 most popular/sold products from completed orders only (excludes Processing and Canceled). */
    public List<AdminMostPopularProductResponse> getAdminMostPopularProducts(int limit) {
        List<Order> completedOrders = orderDao.findByStatus(OrderStatus.COMPLETED);
        return completedOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Comparator.<java.util.Map.Entry<Product, Integer>>comparingInt(java.util.Map.Entry::getValue).reversed()
                        .thenComparing(entry -> entry.getKey().getId()))
                .limit(limit)
                .map(entry -> {
                    Product p = entry.getKey();
                    return new AdminMostPopularProductResponse(
                            p.getId(),
                            p.getName() != null ? p.getName() : p.getDescription(),
                            p.getDescription(),
                            entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    /** Top N most profitable products from completed orders. Profit uses order-item purchased prices (retail - wholesale). */
    public List<AdminMostProfitableProductResponse> getAdminMostProfitableProducts(int limit) {
        List<Order> completedOrders = orderDao.findByStatus(OrderStatus.COMPLETED);
        return completedOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> BigDecimal.valueOf(item.getQuantity())
                                        .multiply(item.getPurchasedRetailPrice().subtract(item.getPurchasedWholesalePrice())),
                                BigDecimal::add
                        )
                ))
                .entrySet().stream()
                .sorted(Comparator.comparing((java.util.Map.Entry<Product, BigDecimal> e) -> e.getValue()).reversed()
                        .thenComparing(e -> e.getKey().getId()))
                .limit(limit)
                .map(entry -> {
                    Product p = entry.getKey();
                    return new AdminMostProfitableProductResponse(
                            p.getId(),
                            p.getName() != null ? p.getName() : p.getDescription(),
                            p.getDescription(),
                            entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    // MAPPERS
    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName() != null ? item.getProduct().getName() : item.getProduct().getDescription(),
                        item.getProduct().getDescription(),
                        item.getQuantity(),
                        item.getPurchasedRetailPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getPlacedTime(),
                order.getStatus().name(),
                items
        );
    }

    private AdminOrderResponse mapToAdminOrderResponse(Order order) {

        List<AdminOrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> new AdminOrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName() != null ? item.getProduct().getName() : item.getProduct().getDescription(),
                        item.getProduct().getDescription(),
                        item.getQuantity(),
                        item.getPurchasedRetailPrice(),
                        item.getPurchasedWholesalePrice()
                ))
                .collect(Collectors.toList());

        return new AdminOrderResponse(
                order.getId(),
                order.getUser().getUsername(),
                order.getPlacedTime(),
                order.getStatus().name(),
                items
        );
    }
}
