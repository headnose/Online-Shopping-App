package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.dto.OrderResponse;
import com.headnose.personalproject.dto.PlaceOrderRequest;
import com.headnose.personalproject.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse order = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrderByUser(id);
        return ResponseEntity.ok(new MessageResponse("Order canceled successfully."));
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
