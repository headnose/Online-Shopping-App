package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.Order;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    private final SessionFactory sessionFactory;

    public OrderDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Order order) {
        sessionFactory.getCurrentSession().persist(order);
    }

    public Order findById(Long id) {
        return sessionFactory.getCurrentSession()
                .get(Order.class, id);
    }

}
