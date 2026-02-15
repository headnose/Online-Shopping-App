package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.TestUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TestUserDao {

    private final SessionFactory sessionFactory;

    public TestUserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(TestUser user) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(user);
    }
}
