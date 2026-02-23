package com.headnose.personalproject.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal purchasedRetailPrice;

    @Column(nullable = false)
    private BigDecimal purchasedWholesalePrice;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasedRetailPrice() {
        return purchasedRetailPrice;
    }

    public void setPurchasedRetailPrice(BigDecimal purchasedRetailPrice) {
        this.purchasedRetailPrice = purchasedRetailPrice;
    }

    public BigDecimal getPurchasedWholesalePrice() {
        return purchasedWholesalePrice;
    }

    public void setPurchasedWholesalePrice(BigDecimal purchasedWholesalePrice) {
        this.purchasedWholesalePrice = purchasedWholesalePrice;
    }
}
