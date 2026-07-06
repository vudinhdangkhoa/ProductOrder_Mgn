package com.example.demo.service.impl;

import java.time.LocalDate;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.ProductionOrder;
import com.example.demo.entity.enums.ProductionOrderStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductionOrderMapper;
import com.example.demo.repository.ProductionOrderRepository;
import com.example.demo.service.interf.ProductionOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository orderRepository;
    private final ProductionOrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ProductionOrderResponse createOrder(CreateProductionOrderRequest request) {
        log.info("Creating new production order: {}", request.getOrderCode());

        if (orderRepository.existsByOrderCode(request.getOrderCode())) {
            //throw new BusinessException("ORDER_EXISTS", "Order code already exists");
        }

        ProductionOrder order = orderMapper.toEntity(request);
        order.setStatus(ProductionOrderStatus.CREATED);
        
        ProductionOrder saved = orderRepository.save(order);
        
        // Bắn event để đồng bộ realtime sau khi commit
       // eventPublisher.publishEvent(new ProductionOrderEvent(saved, "CREATED"));
        
        return orderMapper.toResponse(saved);
    }

    @Override
    public ProductionOrderResponse getOrderById(Long id) {
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));
    }

    @Override
    public Page<ProductionOrderResponse> searchOrders(ProductionOrderStatus status, Long lineId, 
                                                      Long assignedUserId, LocalDate startDate, 
                                                      LocalDate endDate, String keyword, Pageable pageable) {
        return orderRepository.searchOrders(status, lineId, assignedUserId, startDate, endDate, keyword, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductionOrderResponse updateOrder(Long id, UpdateProductionOrderRequest request) {
        log.info("Updating production order id: {}", id);
        ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        // Rule 3: Không sửa lệnh đã Released (Ví dụ logic nghiệp vụ)
        if (order.getStatus() == ProductionOrderStatus.RELEASED) {
            throw new BusinessException("INVALID_STATUS", "Cannot update released order");
        }

        orderMapper.updateEntityFromRequest(request, order);
        ProductionOrder updated = orderRepository.save(order);
        
        //eventPublisher.publishEvent(new ProductionOrderEvent(updated, "UPDATED"));
        
        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Soft deleting production order id: {}", id);
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductionOrder", id);
        }
        orderRepository.softDeleteById(id);
    }
}