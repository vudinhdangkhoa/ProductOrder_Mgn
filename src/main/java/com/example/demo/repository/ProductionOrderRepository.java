package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.ProductionOrder;
import com.example.demo.entity.enums.ProductionOrderStatus;

@Repository
@Transactional(readOnly = true)
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

        Optional<ProductionOrder> findByIdAndIsDeletedFalse(Long id);

        Optional<ProductionOrder> findByOrderCodeAndIsDeletedFalse(String orderCode);

        boolean existsByOrderCode(String orderCode);

        @Query("SELECT po FROM ProductionOrder po " +
                        "WHERE po.isDeleted = false " +
                        "AND (:lineId IS NULL OR po.line.id = :lineId) " +
                      
                "AND (po.status='RELEASED' OR po.status='IN_PROGRESS')")
                        
        List<ProductionOrder> findAllByLineIdAndIsDeletedFalse(
                        @Param("lineId") Long lineId);

        @Query("SELECT po FROM ProductionOrder po " +
                        "WHERE po.isDeleted = false " +
                        "AND (:lineId IS NULL OR po.line.id = :lineId) " +
                        "AND (po.status='DRAFT')")
        List<ProductionOrder> findAllDraftByLineIdAndIsDeletedFalse(
                        @Param("lineId") Long lineId);
        
        Page<ProductionOrder> findAllByIsDeletedFalse(Pageable pageable);

        // JPQL Query cực kỳ quan trọng cho tính năng Lọc nhiều tiêu chí của Dashboard
        @Query("SELECT po FROM ProductionOrder po " +
                        "WHERE po.isDeleted = false " +
                        "AND (:status IS NULL OR po.status = :status) " +
                        "AND (:lineId IS NULL OR po.line.id = :lineId) " +
                        "AND (:assignedUserId IS NULL OR po.assignedUser.id = :assignedUserId) " +
                        "AND (cast(:startDate as date) IS NULL OR po.startDate >= :startDate) " +
                        "AND (cast(:endDate as date) IS NULL OR po.endDate <= :endDate) " +
                        "AND (:keyword IS NULL OR LOWER(po.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<ProductionOrder> searchOrders(@Param("status") ProductionOrderStatus status,
                        @Param("lineId") Long lineId,
                        @Param("assignedUserId") Long assignedUserId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        // Đếm số lượng lệnh theo trạng thái phục vụ Chart/Dashboard
        long countByStatusAndIsDeletedFalse(ProductionOrderStatus status);

        @Modifying
        @Transactional
        @Query("UPDATE ProductionOrder po SET po.isDeleted = true WHERE po.id = :id")
        void softDeleteById(@Param("id") Long id);
}