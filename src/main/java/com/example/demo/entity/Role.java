package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EnumType;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name_role", nullable = false, length = 20)
    private String nameRole;

    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
