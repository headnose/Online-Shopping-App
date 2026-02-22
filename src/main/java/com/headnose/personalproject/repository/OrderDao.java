package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.Order;
import com.headnose.personalproject.entity.OrderStatus;
import com.headnose.personalproject.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Order> findByUser(User user) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Order o where o.user = :user", Order.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Order> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Order", Order.class)
                .getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Order o where o.status = :status", Order.class)
                .setParameter("status", status)
                .getResultList();
    }

}
