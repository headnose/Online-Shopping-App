package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.AdminOrderResponse;
import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.dto.OrderResponse;
import com.headnose.personalproject.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<MessageResponse> completeOrder(@PathVariable Long id) {
        orderService.completeOrderByAdmin(id);
        return ResponseEntity.ok(new MessageResponse("Order completed successfully."));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrderByAdmin(id);
        return ResponseEntity.ok(new MessageResponse("Order canceled successfully."));
    }

    @GetMapping
    public List<AdminOrderResponse> getAllOrders() {
        return orderService.getAllOrdersForAdmin();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderByIdForCurrentUser(id);
    }
}


