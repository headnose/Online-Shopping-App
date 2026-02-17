package com.headnose.personalproject.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class PlaceOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
