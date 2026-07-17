package com.example.demo.dto.response;

import java.time.LocalDateTime;

import com.example.demo.entity.enums.ProductionOrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuditLogPOResponse {

    long id;
    Long productOrderId;
    String productOrderCode;
    Long userId;
    String userName;
    ProductionOrderStatus action;
    LocalDateTime createdAt; 

}
