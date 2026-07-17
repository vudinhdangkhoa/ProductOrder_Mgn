package com.example.demo.mapper;

import java.util.List;

import org.hibernate.query.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.dto.response.AuditLogPOResponse;
import com.example.demo.entity.AuditLogStatusProductionOrder;

@Mapper(componentModel = "spring")
public interface  AuditLogPOMapper {

    @Mapping(source = "productionOrder.id", target = "productOrderId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "productionOrder.orderCode", target = "productOrderCode")
    @Mapping(source = "user.username", target = "userName")
    AuditLogPOResponse toResponse(AuditLogStatusProductionOrder auditLogStatusProductionOrder);

    List<AuditLogPOResponse> toResponsePage(List<AuditLogStatusProductionOrder> auditLogStatusProductionOrders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
   
    @Mapping(target = "isDeleted", ignore = true)
    AuditLogStatusProductionOrder toEntity(AuditLogPOResponse auditLogPOResponse);

}
