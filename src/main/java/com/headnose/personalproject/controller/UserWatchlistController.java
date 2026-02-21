package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.dto.WatchlistProductResponse;
import com.headnose.personalproject.service.WatchlistService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MessageResponse> add(@PathVariable Long productId) {
        watchlistService.addToWatchlist(productId);
        return ResponseEntity.ok(new MessageResponse("Product added to watchlist."));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponse> remove(@PathVariable Long productId) {
        watchlistService.removeFromWatchlist(productId);
        return ResponseEntity.ok(new MessageResponse("Product removed from watchlist."));
    }

    @GetMapping
    public List<WatchlistProductResponse> viewWatchlist() {
        return watchlistService.getWatchlistProducts();
    }
}
