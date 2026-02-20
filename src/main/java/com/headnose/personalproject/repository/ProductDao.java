package com.headnose.personalproject.repository;

import com.headnose.personalproject.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class ProductDao {

    private final SessionFactory sessionFactory;

    public ProductDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Product product) {
        sessionFactory.getCurrentSession().persist(product);
    }

    public Product findById(Long id) {
        return sessionFactory.getCurrentSession()
                .get(Product.class, id);
    }

    public List<Product> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Product", Product.class)
                .getResultList();
    }

    // Criteria example:
    public List<Product> findInStock() {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        cq.select(root)
                .where(cb.greaterThan(root.get("quantity"), 0));

        return session.createQuery(cq).getResultList();
    }

    public void update(Product product) {
        sessionFactory.getCurrentSession().merge(product);
    }

    public void delete(Product product) {
        sessionFactory.getCurrentSession().remove(product);
    }

}
