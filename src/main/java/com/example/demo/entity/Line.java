package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "production_lines")
@Getter
@Setter
@NoArgsConstructor
public class Line extends BaseEntity {
    @Column(name = "line_code", nullable = false, unique = true, length = 20)
    private String lineCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private List<ProductionOrder> productionOrders = new ArrayList<>();
}
