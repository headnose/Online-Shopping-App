package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.OrderResponse;
import com.headnose.personalproject.dto.PlaceOrderRequest;
import com.headnose.personalproject.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user/orders")
public class UserOrderController {

    private final OrderService orderService;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public void placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        orderService.placeOrder(request);
    }

    @PatchMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrderByUser(id);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders() {
        return orderService.getOrdersForCurrentUser();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderByIdForCurrentUser(id);
    }
}
