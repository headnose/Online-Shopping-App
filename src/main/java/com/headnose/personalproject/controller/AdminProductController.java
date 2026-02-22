package com.headnose.personalproject.controller;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.AdminMostPopularProductResponse;
import com.headnose.personalproject.dto.AdminMostProfitableProductResponse;
import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.OrderService;
import com.headnose.personalproject.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final OrderService orderService;

    public AdminProductController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/popular/{limit}")
    public List<AdminMostPopularProductResponse> getMostPopularProducts(@PathVariable int limit) {
        return orderService.getAdminMostPopularProducts(limit);
    }

    @GetMapping("/profit/{limit}")
    public List<AdminMostProfitableProductResponse> getMostProfitableProducts(@PathVariable int limit) {
        return orderService.getAdminMostProfitableProducts(limit);
    }

    @GetMapping("/{id}")
    public Product getProductDetail(@PathVariable Long id) {
        return productService.getProductByIdOrThrow(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addProduct(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok(new MessageResponse(ApiMessages.PRODUCT_CREATED));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateProduct(@PathVariable Long id,
                                                          @RequestBody Product updatedProduct) {
        productService.updateProductById(id, updatedProduct);
        return ResponseEntity.ok(new MessageResponse(ApiMessages.PRODUCT_UPDATED));
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
