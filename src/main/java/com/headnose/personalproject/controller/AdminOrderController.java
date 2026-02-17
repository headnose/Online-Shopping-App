package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.AdminOrderResponse;
import com.headnose.personalproject.service.OrderService;
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
    public void completeOrder(@PathVariable Long id) {
        orderService.completeOrderByAdmin(id);
    }

    @PatchMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrderByAdmin(id);
    }

    @GetMapping
    public List<AdminOrderResponse> getAllOrders() {
        return orderService.getAllOrdersForAdmin();
    }
}
