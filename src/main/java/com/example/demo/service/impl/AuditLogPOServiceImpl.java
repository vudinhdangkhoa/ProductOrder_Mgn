package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.response.AuditLogPOResponse;
import com.example.demo.entity.AuditLogStatusProductionOrder;
import com.example.demo.entity.ProductionOrder;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.AuditLogPOMapper;
import com.example.demo.repository.AuditLogPORepository;
import com.example.demo.service.interf.AuditLogPOService;
import com.example.demo.repository.ProductionOrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogPOServiceImpl implements AuditLogPOService {

    // Implement the methods defined in the AuditLogService interface
    private final AuditLogPORepository auditLogPORepository;
    private final AuditLogPOMapper auditLogPOMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final UserRepository userRepository;

    @Override
    public List<AuditLogPOResponse> getAllByProductionOrderId(Long productionOrderId) {
        // Implementation logic to retrieve audit logs by production order ID
        return auditLogPOMapper.toResponsePage(auditLogPORepository.findAllByProductionOrderIdAndIsDeletedFalse(productionOrderId));
    }

    @Override
@Transactional
public void saveAuditLog(AuditLogPOResponse auditLogPOResponse) {
    // Lấy entity từ DB
    ProductionOrder productionOrder = productionOrderRepository.findById(auditLogPOResponse.getProductOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", auditLogPOResponse.getProductOrderId()));
    
    User user = userRepository.findById(auditLogPOResponse.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", auditLogPOResponse.getUserId()));
    
    // Tạo entity thủ công
    AuditLogStatusProductionOrder auditLog = new AuditLogStatusProductionOrder();
    auditLog.setProductionOrder(productionOrder);  // Set relationship
    auditLog.setUser(user);                        // Set relationship
    auditLog.setAction(auditLogPOResponse.getAction());
       
    
    // Lưu entity
    auditLogPORepository.save(auditLog);
}

} 
