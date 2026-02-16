package com.headnose.personalproject.service;

import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.repository.ProductDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void addProduct(Product product) {
        productDao.save(product);
    }

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productDao.findInStock();
    }

    public Product getById(Long id) {
        return productDao.findById(id);
    }

    public void updateProduct(Product product) {
        productDao.update(product);
    }
}
