package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class UserProductResponse {

    private Long id;
    private String description;
    private BigDecimal retailPrice;

    public UserProductResponse(Long id, String description, BigDecimal retailPrice) {
        this.id = id;
        this.description = description;
        this.retailPrice = retailPrice;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }
}
