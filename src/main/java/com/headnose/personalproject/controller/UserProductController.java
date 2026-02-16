package com.headnose.personalproject.controller;

import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/products")
public class UserProductController {

    private final ProductService productService;

    public UserProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> viewAvailableProducts() {
        return productService.getAvailableProducts();
    }
}
