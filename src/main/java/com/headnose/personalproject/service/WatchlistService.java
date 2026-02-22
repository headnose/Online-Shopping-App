package com.headnose.personalproject.service;

import com.headnose.personalproject.dto.WatchlistProductResponse;
import com.headnose.personalproject.entity.*;
import com.headnose.personalproject.exception.ProductNotAvailableException;
import com.headnose.personalproject.repository.*;
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
    private final UserDao userDao;
    private final ProductDao productDao;

    public WatchlistService(WatchlistDao watchlistDao,
                            UserDao userDao,
                            ProductDao productDao) {
        this.watchlistDao = watchlistDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    public void addToWatchlist(Long productId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("User {} adding product {} to watchlist", username, productId);

        User user = userDao.findByUsername(username);
        Product product = productDao.findById(productId);

        if (product == null) {
            throw new ProductNotAvailableException("Product not found.");
        }
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new ProductNotAvailableException("Product is out of stock and cannot be added to watchlist.");
        }

        Watchlist existing =
                watchlistDao.findByUserAndProduct(user, product);

        if (existing != null) {
            log.warn("Product {} already in watchlist for user {}", productId, username);
            return;
        }

        // Re-fetch product to ensure we have current quantity before saving (avoids stale/cached value)
        Product current = productDao.findById(productId);
        if (current == null || current.getQuantity() == null || current.getQuantity() <= 0) {
            throw new ProductNotAvailableException("Product is out of stock and cannot be added to watchlist.");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setProduct(product);

        watchlistDao.save(watchlist);

        log.info("Product {} added to watchlist for user {}", productId, username);
    }

    public void removeFromWatchlist(Long productId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.info("User {} removing product {} from watchlist", username, productId);

        User user = userDao.findByUsername(username);
        Product product = productDao.findById(productId);

        Watchlist existing =
                watchlistDao.findByUserAndProduct(user, product);

        if (existing != null) {
            watchlistDao.delete(existing);
            log.info("Product {} removed from watchlist for user {}", productId, username);
        }
    }

        public List<WatchlistProductResponse> getWatchlistProducts () {

            String username = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            User user = userDao.findByUsername(username);

            return watchlistDao.findByUser(user)
                    .stream()
                    .map(Watchlist::getProduct)
                    .filter(product -> product.getQuantity() > 0)
                    .map(product -> new WatchlistProductResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getRetailPrice(),
                            product.getQuantity()
                    ))
                    .collect(Collectors.toList());
        }
    }

