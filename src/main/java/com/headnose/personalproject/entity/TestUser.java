package com.headnose.personalproject.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "test_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
