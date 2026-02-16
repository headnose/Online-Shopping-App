package com.headnose.personalproject.controller;

import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public String addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return "Product added successfully";
    }
}
