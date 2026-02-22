package com.headnose.personalproject.service;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.exception.ProductNotAvailableException;
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

    
    public void updateProductById(Long id, Product updatedProduct) {
        Product existing = getProductByIdOrThrow(id);
        if (updatedProduct.getName() != null) {
            existing.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existing.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getWholesalePrice() != null) {
            existing.setWholesalePrice(updatedProduct.getWholesalePrice());
        }
        if (updatedProduct.getRetailPrice() != null) {
            existing.setRetailPrice(updatedProduct.getRetailPrice());
        }
        if (updatedProduct.getQuantity() != null) {
            existing.setQuantity(updatedProduct.getQuantity());
        }
        productDao.update(existing);
    }

    public Product getProductById(Long id) {
        return productDao.findById(id);
    }

    
    public Product getProductByIdOrThrow(Long id) {
        Product product = productDao.findById(id);
        if (product == null) {
            throw new ProductNotAvailableException(ApiMessages.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    public Product getInStockProductById(Long id) {
        Product product = productDao.findById(id);
        if (product == null || product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new ProductNotAvailableException(ApiMessages.PRODUCT_NOT_AVAILABLE);
        }
        return product;
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
            throw new IllegalArgumentException(ApiMessages.PRODUCT_NOT_FOUND);
        }
        productDao.delete(product);
    }
}
