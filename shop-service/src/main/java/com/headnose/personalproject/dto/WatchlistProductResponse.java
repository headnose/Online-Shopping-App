package com.headnose.personalproject.dto;

import java.math.BigDecimal;

public class WatchlistProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal retailPrice;

    public WatchlistProductResponse(Long id,
                                    String name,
                                    String description,
                                    BigDecimal retailPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getRetailPrice() { return retailPrice; }
}
