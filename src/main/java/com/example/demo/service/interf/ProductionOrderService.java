package com.example.demo.service.interf;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.AuditLogPOResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.enums.ProductionOrderStatus;

public interface ProductionOrderService {
    ProductionOrderResponse createOrder(CreateProductionOrderRequest request,Long userId);
    List<AuditLogPOResponse> getOrderById(Long id);
    
    ProductionOrderResponse updateOrder(Long id, UpdateProductionOrderRequest request,Long userId);
    void deleteOrder(Long id, Long userId);

    Page<ProductionOrderResponse> getOrder(Pageable pageable,
                                           Optional<ProductionOrderStatus> status,
                                           Optional<Long> lineId,
                                           Optional<String> assignedUserUserName,
                                           Optional<LocalDate> startDate,
                                           Optional<LocalDate> endDate,
                                           Optional<String> POCode);

    ProductionOrderResponse releaseOrder(Long id, String userName);
    ProductionOrderResponse completeOrder(Long id, Long userId, ProductionOrderStatus request);
    ProductionOrderResponse cancelOrder(Long id, Long userId, String reason);
    Map<String,List<ProductionOrderResponse>> listConflictOrdersDRAFT(Long lineId);
}