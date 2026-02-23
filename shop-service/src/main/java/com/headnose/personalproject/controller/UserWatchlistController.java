package com.headnose.personalproject.controller;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.dto.WatchlistProductResponse;
import com.headnose.personalproject.dto.WatchlistResponse;
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
        return ResponseEntity.ok(new MessageResponse(ApiMessages.PRODUCT_ADDED_TO_WATCHLIST));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponse> remove(@PathVariable Long productId) {
        watchlistService.removeFromWatchlist(productId);
        return ResponseEntity.ok(new MessageResponse(ApiMessages.PRODUCT_REMOVED_FROM_WATCHLIST));
    }

    @GetMapping
    public ResponseEntity<WatchlistResponse> viewWatchlist() {
        List<WatchlistProductResponse> items = watchlistService.getWatchlistProducts();
        String message = items.isEmpty()
                ? ApiMessages.WATCHLIST_EMPTY
                : String.format(ApiMessages.WATCHLIST_ITEMS_COUNT, items.size());
        return ResponseEntity.ok(new WatchlistResponse(message, items));
    }
}
