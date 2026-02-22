package com.headnose.personalproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headnose.personalproject.dto.*;
import com.headnose.personalproject.entity.*;
import com.headnose.personalproject.exception.NotEnoughInventoryException;
import com.headnose.personalproject.repository.OrderDao;
import com.headnose.personalproject.repository.ProductDao;
import com.headnose.personalproject.repository.UserDao;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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


    public OrderResponse placeOrder(PlaceOrderRequest request) {

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
                        "Not enough inventory for product ID: "
                                + product.getId());
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

        return mapToOrderResponse(order);
    }


    public void cancelOrderByUser(Long orderId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("User {} attempting to cancel order {}", username, orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Cancel failed: Order {} not found", orderId);
            throw new RuntimeException("Order not found");
        }

        if (!order.getUser().getUsername().equals(username)) {
            log.warn("Cancel failed: User {} does not own order {}", username, orderId);
            throw new RuntimeException("Access denied: not your order");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Cancel failed: Order {} is not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException("Only PROCESSING orders can be canceled");
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


    public void completeOrderByAdmin(Long orderId) {

        log.info("Admin attempting to complete order {}", orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Admin completion failed: Order {} not found", orderId);
            throw new RuntimeException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Admin completion failed: Order {} not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException("Only PROCESSING orders can be completed");
        }

        order.setStatus(OrderStatus.COMPLETED);

        log.info("Order {} marked as COMPLETED by admin", orderId);
    }

    public void cancelOrderByAdmin(Long orderId) {

        log.info("Admin attempting to cancel order {}", orderId);

        Order order = orderDao.findById(orderId);

        if (order == null) {
            log.warn("Admin cancel failed: Order {} not found", orderId);
            throw new RuntimeException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            log.warn("Admin cancel failed: Order {} not PROCESSING (status: {})",
                    orderId, order.getStatus());
            throw new RuntimeException("Only PROCESSING orders can be canceled");
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
            throw new RuntimeException("Order not found");
        }

        if (!order.getUser().getUsername().equals(username)) {
            log.warn("Access denied for user {} on order {}", username, orderId);
            throw new RuntimeException("Access denied: not your order");
        }

        return mapToOrderResponse(order);
    }

    public List<AdminOrderResponse> getAllOrdersForAdmin() {

        log.info("Admin fetching all orders");

        List<Order> orders = orderDao.findAll();

        log.info("Admin retrieved {} total orders", orders.size());

        return orders.stream()
                .map(this::mapToAdminOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
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
