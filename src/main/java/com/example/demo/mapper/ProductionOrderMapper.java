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
    ProductionOrderResponse toResponse(ProductionOrder productionOrder);

    List<ProductionOrderResponse> toResponseList(List<ProductionOrder> productionOrders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "line", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "status", ignore = true) // Status mặc định là DRAFT khi tạo
    ProductionOrder toEntity(CreateProductionOrderRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "line", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    void updateEntityFromRequest(UpdateProductionOrderRequest req, @MappingTarget ProductionOrder productionOrder);
}