package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.RecentPurchaseItemResponse;
import com.headnose.personalproject.dto.UserProductResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.OrderService;
import com.headnose.personalproject.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user/products")
public class UserProductController {

    private final ProductService productService;
    private final OrderService orderService;

    public UserProductController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public List<UserProductResponse> getAllInStockProducts() {

        return productService.getInStockProducts()
                .stream()
                .map(p -> new UserProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getRetailPrice()
                ))
                .collect(Collectors.toList());

    }


    @GetMapping("/{id}")
    public UserProductResponse getProductDetail(@PathVariable Long id) {

        Product p = productService.getInStockProductById(id);

        return new UserProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getRetailPrice()
        );
    }

    @GetMapping("/frequent/{limit}")
    public List<RecentPurchaseItemResponse> getMostFrequentlyPurchased(@PathVariable int limit) {
        return orderService.getMostFrequentlyPurchasedProducts(limit);
    }
}
