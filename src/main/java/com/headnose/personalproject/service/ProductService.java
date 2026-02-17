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

    public void saveProduct(Product product) {
        productDao.save(product);
    }

    public void updateProduct(Product product) {
        productDao.update(product);
    }

    public Product getProductById(Long id) {
        return productDao.findById(id);
    }

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public List<Product> getInStockProducts() {
        return productDao.findInStock();
    }

    public void deleteProduct(Long id) {
        Product product = productDao.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        productDao.delete(product);
    }
}
