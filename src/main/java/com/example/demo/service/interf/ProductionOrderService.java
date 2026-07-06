package com.example.demo.service;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.enums.ProductionOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ProductionOrderService {
    ProductionOrderResponse createOrder(CreateProductionOrderRequest request);
    ProductionOrderResponse getOrderById(Long id);
    Page<ProductionOrderResponse> searchOrders(ProductionOrderStatus status, Long lineId, 
                                               Long assignedUserId, LocalDate startDate, 
                                               LocalDate endDate, String keyword, Pageable pageable);
    ProductionOrderResponse updateOrder(Long id, UpdateProductionOrderRequest request);
    void deleteOrder(Long id);
}