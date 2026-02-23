package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.*;
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

    public Watchlist findByUserAndProduct(User user, Product product) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Watchlist w where w.user = :user and w.product = :product", Watchlist.class)
                .setParameter("user", user)
                .setParameter("product", product)
                .uniqueResult();
    }

    public List<Watchlist> findByUser(User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Watchlist w where w.user = :user", Watchlist.class)
                .setParameter("user", user)
                .getResultList();
    }
}
