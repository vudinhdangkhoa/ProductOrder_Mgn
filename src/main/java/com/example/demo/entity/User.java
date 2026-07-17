package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "password", nullable = true, length = 255)
    private String password = "1";

    @Column(name = "Sub", length = 100, nullable = true)
    private String sub;

    @Column(name= "department", length = 100, nullable = true)
    private String department;

    @Column(name= "position", length = 100, nullable = true)
    private String position;

    @Column(name= "avatar", length = 255, nullable = true)
    private String avatar;

    @Column(name= "telegram", length = 100, nullable = true)
    private String telegram;

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private List<ProductionOrder> assignedProductionOrders = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AuditLogStatusProductionOrder> statusUpdates = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens = new ArrayList<>();


}

