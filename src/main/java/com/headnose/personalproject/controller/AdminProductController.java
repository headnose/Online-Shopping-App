package com.headnose.personalproject.controller;

import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products (including out-of-stock)
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // POST add new product
    @PostMapping
    public void addProduct(@RequestBody Product product) {
        productService.saveProduct(product);
    }

    // PUT update product
    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id,
                              @RequestBody Product updatedProduct) {

        Product existing = productService.getProductById(id);

        existing.setDescription(updatedProduct.getDescription());
        existing.setWholesalePrice(updatedProduct.getWholesalePrice());
        existing.setRetailPrice(updatedProduct.getRetailPrice());
        existing.setQuantity(updatedProduct.getQuantity());

        productService.updateProduct(existing);
    }
}
