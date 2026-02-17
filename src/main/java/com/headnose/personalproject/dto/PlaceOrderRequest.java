package com.headnose.personalproject.dto;

import java.util.List;

public class PlaceOrderRequest {

    private List<OrderItemRequest> items;

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
