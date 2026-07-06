package com.example.demo.service.event;

import com.example.demo.entity.ProductionOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductionOrderEvent extends ApplicationEvent {
    private final ProductionOrder productionOrder;
    private final String action; // Ví dụ: "CREATED", "UPDATED"

    public ProductionOrderEvent(Object source, ProductionOrder productionOrder, String action) {
        super(source);
        this.productionOrder = productionOrder;
        this.action = action;
    }
}