package com.headnose.personalproject.dto;

import java.util.List;

public class WatchlistResponse {

    private String message;
    private List<WatchlistProductResponse> items;

    public WatchlistResponse(String message, List<WatchlistProductResponse> items) {
        this.message = message;
        this.items = items;
    }

    public String getMessage() {
        return message;
    }

    public List<WatchlistProductResponse> getItems() {
        return items;
    }
}
