package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "products")
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category_code", nullable = false, unique = true, length = 20)
    private String categoryCode;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}
