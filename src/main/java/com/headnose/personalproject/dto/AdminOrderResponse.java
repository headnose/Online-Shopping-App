package com.headnose.personalproject.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderResponse {

    private Long orderId;
    private String username;
    private LocalDateTime placedTime;
    private String status;
    private List<AdminOrderItemResponse> items;

    public AdminOrderResponse(Long orderId,
                              String username,
                              LocalDateTime placedTime,
                              String status,
                              List<AdminOrderItemResponse> items) {
        this.orderId = orderId;
        this.username = username;
        this.placedTime = placedTime;
        this.status = status;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public LocalDateTime getPlacedTime() { return placedTime; }
    public String getStatus() { return status; }
    public List<AdminOrderItemResponse> getItems() { return items; }
}
