package com.headnose.personalproject.dto;

public class AdminMostPopularProductResponse {

    private Long productId;
    private String productName;
    private String description;
    private int unitsSold;

    public AdminMostPopularProductResponse(Long productId, String productName, String description, int unitsSold) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.unitsSold = unitsSold;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public int getUnitsSold() { return unitsSold; }
}
