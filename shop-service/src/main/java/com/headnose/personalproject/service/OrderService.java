package com.headnose.personalproject.service;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.*;
import com.headnose.personalproject.entity.*;
import com.headnose.personalproject.exception.NotEnoughInventoryException;
import com.headnose.personalproject.repository.OrderDao;
import com.headnose.personalproject.repository.ProductDao;
import com.headnose.personalproject.security.CustomUserDetails;

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

    private final OrderDao orderDao;
    private final ProductDao productDao;

    public OrderService(OrderDao orderDao,
                        ProductDao productDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    // =========================================
    // PLACE ORDER
    // =========================================
    public void placeOrder(PlaceOrderRequest request) {

        Long userId = extractUserIdFromSecurityContext();

        Order order = new Order();
        order.setUserId(userId);
        order.setPlacedTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productDao.findById(itemRequest.getProductId());

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new NotEnoughInventoryException(
                        String.format(ApiMessages.NOT_ENOUGH_INVENTORY_FORMAT, product.getId()));
            }

            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPurchasedRetailPrice(product.getRetailPrice());
            orderItem.setPurchasedWholesalePrice(product.getWholesalePrice());

            order.addOrderItem(orderItem);
        }

        orderDao.save(order);
    }

    // =========================================
    // USER CANCEL ORDER
    // =========================================
    public void cancelOrderByUser(Long orderId) {

        Long userId = extractUserIdFromSecurityContext();
        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException(ApiMessages.ORDER_ACCESS_DENIED);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_CANCEL);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);
    }

    // =========================================
    // ADMIN COMPLETE ORDER
    // =========================================
    public void completeOrderByAdmin(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_COMPLETE);
        }

        order.setStatus(OrderStatus.COMPLETED);
    }

    // =========================================
    // ADMIN CANCEL ORDER
    // =========================================
    public void cancelOrderByAdmin(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException(ApiMessages.ORDER_ONLY_PROCESSING_CAN_CANCEL);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELED);
    }

    // =========================================
    // USER VIEW ORDERS
    // =========================================
    public List<OrderResponse> getOrdersForCurrentUser() {

        Long userId = extractUserIdFromSecurityContext();
        List<Order> orders = orderDao.findByUserId(userId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByIdForCurrentUser(Long orderId) {

        Long userId = extractUserIdFromSecurityContext();
        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException(ApiMessages.ORDER_ACCESS_DENIED);
        }

        return mapToOrderResponse(order);
    }

    public List<RecentPurchaseItemResponse> getMostFrequentlyPurchasedProducts(int limit) {

        Long userId = extractUserIdFromSecurityContext();

        List<Order> orders = orderDao.findByUserId(userId).stream()
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
                            p.getName(),
                            p.getDescription(),
                            p.getRetailPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<RecentPurchaseItemResponse> getMostRecentlyPurchasedItems(int limit) {

        Long userId = extractUserIdFromSecurityContext();

        List<Order> orders = orderDao.findByUserId(userId).stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELED)
                .sorted(Comparator.comparing(Order::getPlacedTime).reversed())
                .collect(Collectors.toList());

        return orders.stream()
                .flatMap(o -> o.getOrderItems().stream()
                        .map(item -> new java.util.AbstractMap.SimpleEntry<>(item, o.getPlacedTime())))
                .sorted(Comparator.comparing(java.util.Map.Entry<OrderItem, java.time.LocalDateTime>::getValue).reversed())
                .limit(limit)
                .map(e -> {
                    OrderItem item = e.getKey();
                    Product p = item.getProduct();
                    return new RecentPurchaseItemResponse(
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            item.getPurchasedRetailPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    // =========================================
    // ADMIN VIEW
    // =========================================
    public List<AdminOrderResponse> getAllOrdersForAdmin() {
        return orderDao.findAll()
                .stream()
                .map(this::mapToAdminOrderResponse)
                .collect(Collectors.toList());
    }

    public AdminOrderResponse getOrderByIdForAdmin(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException(ApiMessages.ORDER_NOT_FOUND);
        }

        return mapToAdminOrderResponse(order);
    }

    // =========================================
    // ADMIN STATS (REQUIRED)
    // =========================================
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
                            p.getName(),
                            p.getDescription(),
                            entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<AdminMostProfitableProductResponse> getAdminMostProfitableProducts(int limit) {

        List<Order> completedOrders = orderDao.findByStatus(OrderStatus.COMPLETED);

        return completedOrders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> BigDecimal.valueOf(item.getQuantity())
                                        .multiply(item.getPurchasedRetailPrice()
                                                .subtract(item.getPurchasedWholesalePrice())),
                                BigDecimal::add
                        )
                ))
                .entrySet().stream()
                .sorted(Comparator.comparing(
                        (java.util.Map.Entry<Product, BigDecimal> e) -> e.getValue()
                ).reversed().thenComparing(e -> e.getKey().getId()))
                .limit(limit)
                .map(entry -> {
                    Product p = entry.getKey();
                    return new AdminMostProfitableProductResponse(
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    // =========================================
    // HELPERS
    // =========================================
    private Long extractUserIdFromSecurityContext() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        throw new RuntimeException("Invalid authentication principal");
    }

    // =========================================
    // MAPPERS
    // =========================================
    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
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
                        item.getProduct().getName(),
                        item.getProduct().getDescription(),
                        item.getQuantity(),
                        item.getPurchasedRetailPrice(),
                        item.getPurchasedWholesalePrice()
                ))
                .collect(Collectors.toList());

        return new AdminOrderResponse(
                order.getId(),
                order.getUserId().toString(),
                order.getPlacedTime(),
                order.getStatus().name(),
                items
        );
    }
}