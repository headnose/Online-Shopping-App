package com.headnose.personalproject.service;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.WatchlistProductResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.entity.Watchlist;
import com.headnose.personalproject.exception.ProductNotAvailableException;
import com.headnose.personalproject.repository.ProductDao;
import com.headnose.personalproject.repository.WatchlistDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WatchlistService {

    private static final Logger log =
            LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistDao watchlistDao;
    private final ProductDao productDao;

    public WatchlistService(WatchlistDao watchlistDao,
                            ProductDao productDao) {
        this.watchlistDao = watchlistDao;
        this.productDao = productDao;
    }

    private Long extractUserId() {
        String subject = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return Long.parseLong(subject);
    }

    public void addToWatchlist(Long productId) {

        Long userId = extractUserId();
        log.info("User {} adding product {} to watchlist", userId, productId);

        Product product = productDao.findById(productId);

        if (product == null) {
            throw new ProductNotAvailableException(ApiMessages.PRODUCT_NOT_FOUND);
        }

        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new ProductNotAvailableException(ApiMessages.PRODUCT_OUT_OF_STOCK_WATCHLIST);
        }

        Watchlist existing =
                watchlistDao.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            log.warn("Product {} already in watchlist for user {}", productId, userId);
            return;
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setProduct(product);

        watchlistDao.save(watchlist);

        log.info("Product {} added to watchlist for user {}", productId, userId);
    }

    public void removeFromWatchlist(Long productId) {

        Long userId = extractUserId();

        Watchlist existing =
                watchlistDao.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            watchlistDao.delete(existing);
            log.info("Product {} removed from watchlist for user {}", productId, userId);
        }
    }

    public List<WatchlistProductResponse> getWatchlistProducts() {

        Long userId = extractUserId();

        return watchlistDao.findByUserId(userId)
                .stream()
                .map(Watchlist::getProduct)
                .filter(product -> product.getQuantity() > 0)
                .map(product -> new WatchlistProductResponse(
                        product.getId(),
                        product.getName() != null ? product.getName() : product.getDescription(),
                        product.getDescription(),
                        product.getRetailPrice()
                ))
                .collect(Collectors.toList());
    }
}