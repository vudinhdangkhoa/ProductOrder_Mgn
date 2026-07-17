package com.example.demo.service.interf;

import java.util.List;

import com.example.demo.dto.response.AuditLogPOResponse;

public interface  AuditLogPOService {

 List<AuditLogPOResponse> getAllByProductionOrderId(Long productionOrderId);   
 void saveAuditLog(AuditLogPOResponse auditLogPOResponse);
}
