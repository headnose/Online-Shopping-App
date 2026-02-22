package com.headnose.personalproject.controller;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.dto.MessageResponse;
import com.headnose.personalproject.entity.Product;
import com.headnose.personalproject.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
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
