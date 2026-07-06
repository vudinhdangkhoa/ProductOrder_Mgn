package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.entity.enums.ProductionOrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status_production_orders")
@Getter
@Setter
@NoArgsConstructor
public class StatusProductionOrder extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ProductionOrderStatus action= ProductionOrderStatus.DRAFT;

    @Column(name = "date_update")
    private LocalDateTime dateUpdate = LocalDateTime.now();
}
