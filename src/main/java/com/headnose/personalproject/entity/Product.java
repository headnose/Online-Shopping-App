package com.headnose.personalproject.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal wholesalePrice;

    @Column(nullable = false)
    private BigDecimal retailPrice;

    @Column(nullable = false)
    private Integer quantity;
}
