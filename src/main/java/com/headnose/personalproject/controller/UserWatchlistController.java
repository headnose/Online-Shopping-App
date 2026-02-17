package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.WatchlistProductResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.WatchlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/watchlist")
public class UserWatchlistController {

    private final WatchlistService watchlistService;

    public UserWatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping("/{productId}")
    public void add(@PathVariable Long productId) {
        watchlistService.addToWatchlist(productId);
    }

    @DeleteMapping("/{productId}")
    public void remove(@PathVariable Long productId) {
        watchlistService.removeFromWatchlist(productId);
    }

    @GetMapping
    public List<WatchlistProductResponse> viewWatchlist() {
        return watchlistService.getWatchlistProducts();
    }
}
