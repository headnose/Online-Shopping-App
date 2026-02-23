package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class RecentPurchaseItemResponse {

    private Long productId;
    private String productName;
    private String description;
    private BigDecimal purchasedRetailPrice;

    public RecentPurchaseItemResponse(Long productId,
                                      String productName,
                                      String description,
                                      BigDecimal purchasedRetailPrice) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.purchasedRetailPrice = purchasedRetailPrice;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public BigDecimal getPurchasedRetailPrice() { return purchasedRetailPrice; }
}
