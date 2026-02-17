package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class AdminOrderItemResponse {

    private Long productId;
    private String description;
    private Integer quantity;
    private BigDecimal purchasedRetailPrice;
    private BigDecimal purchasedWholesalePrice;

    public AdminOrderItemResponse(Long productId,
                                  String description,
                                  Integer quantity,
                                  BigDecimal purchasedRetailPrice,
                                  BigDecimal purchasedWholesalePrice) {
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
        this.purchasedRetailPrice = purchasedRetailPrice;
        this.purchasedWholesalePrice = purchasedWholesalePrice;
    }

    public Long getProductId() { return productId; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPurchasedRetailPrice() { return purchasedRetailPrice; }
    public BigDecimal getPurchasedWholesalePrice() { return purchasedWholesalePrice; }
}
