package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class AdminMostProfitableProductResponse {

    private Long productId;
    private String productName;
    private String description;
    private BigDecimal totalProfit;

    public AdminMostProfitableProductResponse(Long productId, String productName, String description, BigDecimal totalProfit) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.totalProfit = totalProfit;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public BigDecimal getTotalProfit() { return totalProfit; }
}
