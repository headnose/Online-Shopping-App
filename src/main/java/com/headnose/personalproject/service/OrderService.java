package com.headnose.personalproject.service;

import com.headnose.personalproject.dto.OrderItemRequest;
import com.headnose.personalproject.dto.PlaceOrderRequest;
import com.headnose.personalproject.entity.*;
import com.headnose.personalproject.exception.NotEnoughInventoryException;
import com.headnose.personalproject.repository.OrderDao;
import com.headnose.personalproject.repository.ProductDao;
import com.headnose.personalproject.repository.UserDao;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class OrderService {

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

    public void placeOrder(PlaceOrderRequest request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userDao.findByUsername(username);

        Order order = new Order();
        order.setUser(user);
        order.setPlacedTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productDao.findById(itemRequest.getProductId());

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new NotEnoughInventoryException(
                        "Not enough inventory for product ID: "
                                + product.getId());
            }

            // Deduct stock
            product.setQuantity(
                    product.getQuantity() - itemRequest.getQuantity()
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

            // Snapshot prices
            orderItem.setPurchasedRetailPrice(product.getRetailPrice());
            orderItem.setPurchasedWholesalePrice(product.getWholesalePrice());

            order.addOrderItem(orderItem);
        }

        orderDao.save(order);
    }

    public void cancelOrderByUser(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        // Ensure only PROCESSING orders can be canceled
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Only PROCESSING orders can be canceled");
        }

        // Restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELED);
    }

    public void completeOrderByAdmin(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Only PROCESSING orders can be completed");
        }

        order.setStatus(OrderStatus.COMPLETED);
    }

    public void cancelOrderByAdmin(Long orderId) {

        Order order = orderDao.findById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Only PROCESSING orders can be canceled");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELED);
    }
}
