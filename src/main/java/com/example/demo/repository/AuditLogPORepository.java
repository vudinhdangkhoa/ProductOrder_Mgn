package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AuditLogStatusProductionOrder;

@Repository

public interface AuditLogPORepository extends JpaRepository<AuditLogStatusProductionOrder, Long>{

  

    List<AuditLogStatusProductionOrder> findAllByProductionOrderIdAndIsDeletedFalse(Long productionOrderId);

   
}
