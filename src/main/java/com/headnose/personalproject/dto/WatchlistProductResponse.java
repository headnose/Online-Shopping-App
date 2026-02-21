package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class WatchlistProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal retailPrice;
    private Integer quantity;

    public WatchlistProductResponse(Long id,
                                    String name,
                                    String description,
                                    BigDecimal retailPrice,
                                    Integer quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getRetailPrice() { return retailPrice; }
    public Integer getQuantity() { return quantity; }
}
