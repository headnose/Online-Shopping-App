package com.headnose.personalproject.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private LocalDateTime placedTime;
    private String status;
    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId,
                         LocalDateTime placedTime,
                         String status,
                         List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.placedTime = placedTime;
        this.status = status;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public LocalDateTime getPlacedTime() { return placedTime; }
    public String getStatus() { return status; }
    public List<OrderItemResponse> getItems() { return items; }
}
