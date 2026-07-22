package com.example.demo.mapper;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.ProductionOrder;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "line.id", target = "lineId")
    @Mapping(source = "line.name", target = "lineName")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedUser.fullName", target = "assignedUserName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "orderCode", target = "orderCode")
    ProductionOrderResponse toResponse(ProductionOrder productionOrder);
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "line.id", target = "lineId")
    @Mapping(source = "line.name", target = "lineName")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedUser.fullName", target = "assignedUserName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "orderCode", target = "orderCode")
    List<ProductionOrderResponse> toResponseList(List<ProductionOrder> productionOrders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
   
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "status", ignore = true) // Status mặc định là DRAFT khi tạo
    ProductionOrder toEntity(CreateProductionOrderRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "line", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target= "orderCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromRequest(UpdateProductionOrderRequest req, @MappingTarget ProductionOrder productionOrder);
}