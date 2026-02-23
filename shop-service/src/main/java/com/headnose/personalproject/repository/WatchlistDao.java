package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.Watchlist;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WatchlistDao {

    private final SessionFactory sessionFactory;

    public WatchlistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Watchlist watchlist) {
        sessionFactory.getCurrentSession().persist(watchlist);
    }

    public void delete(Watchlist watchlist) {
        sessionFactory.getCurrentSession().remove(watchlist);
    }

    public Watchlist findByUserIdAndProductId(Long userId, Long productId) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Watchlist w where w.userId = :userId and w.product.id = :productId", Watchlist.class)
                .setParameter("userId", userId)
                .setParameter("productId", productId)
                .uniqueResult();
    }

    public List<Watchlist> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Watchlist w where w.userId = :userId", Watchlist.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}