package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    public User findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.select(root)
                .where(cb.equal(root.get("username"), username));

        return session.createQuery(cq).uniqueResult();
    }

    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.select(root)
                .where(cb.equal(root.get("email"), email));

        return session.createQuery(cq).uniqueResult();
    }

    public User findById(Long id) {
        return sessionFactory.getCurrentSession().get(User.class, id);
    }

    public List<User> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from User", User.class)
                .getResultList();
    }
}
