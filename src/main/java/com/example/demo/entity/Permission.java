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
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission extends BaseEntity {
    @Column(name = "name_permission", nullable = false, length = 25)
    private String namePermission;

    @Column(name = "description", length = 50)
    private String description;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
