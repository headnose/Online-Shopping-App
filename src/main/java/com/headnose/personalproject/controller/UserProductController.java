package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.UserProductResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/products")
public class UserProductController {

    private final ProductService productService;

    public UserProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all in-stock products (buyer view)
    @GetMapping
    public List<UserProductResponse> getAllInStockProducts() {

        return productService.getInStockProducts()
                .stream()
                .map(p -> new UserProductResponse(
                        p.getId(),
                        p.getDescription(),
                        p.getRetailPrice()
                ))
                .collect(Collectors.toList());

    }


    // GET product detail (buyer view)
    @GetMapping("/{id}")
    public Product getProductDetail(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
