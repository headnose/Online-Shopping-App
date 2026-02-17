package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long productId;
    private String description;
    private Integer quantity;
    private BigDecimal purchasedRetailPrice;

    public OrderItemResponse(Long productId,
                             String description,
                             Integer quantity,
                             BigDecimal purchasedRetailPrice) {
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
        this.purchasedRetailPrice = purchasedRetailPrice;
    }

    public Long getProductId() { return productId; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPurchasedRetailPrice() { return purchasedRetailPrice; }
}
