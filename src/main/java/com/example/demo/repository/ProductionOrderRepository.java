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

import com.example.demo.dto.response.LineStatusCountResponse;
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
                        "AND (po.status='RELEASED' OR po.status='IN_PROGRESS' OR po.status='DRAFT')")
        Page<ProductionOrder> findAllByLineIdAndIsDeletedFalse(Long lineId, Pageable pageable);

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

        @Query("SELECT COUNT(po) FROM ProductionOrder po " +
                        "WHERE po.isDeleted = false " +
                        "AND po.status = :status " +
                        "AND ((FUNCTION('MONTH', po.startDate) = :month AND FUNCTION('YEAR', po.startDate) = :year) " +
                        "OR (FUNCTION('MONTH', po.endDate) = :month AND FUNCTION('YEAR', po.endDate) = :year))")
        Long countByStatusByMonth(@Param("status") ProductionOrderStatus status,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT new com.example.demo.dto.response.LineStatusCountResponse(l.name, po.status, COUNT(po)) " +
                        "FROM ProductionOrder po " +
                        "JOIN po.line l " +
                        "WHERE po.isDeleted = false " +
                        "AND ((FUNCTION('MONTH', po.startDate) = :month AND FUNCTION('YEAR', po.startDate) = :year) " +
                        "OR (FUNCTION('MONTH', po.endDate) = :month AND FUNCTION('YEAR', po.endDate) = :year)) " +
                        "GROUP BY l.name, po.status")
        List<LineStatusCountResponse> countByLineAndStatus(@Param("month") int month, @Param("year") int year);

        @Query("SELECT l.name, po.status, po.orderCode, po.startDate, po.endDate, po.quantity, p.name, u.fullName " +
                        "FROM ProductionOrder po " +
                        "JOIN po.line l " +
                        "JOIN po.product p " +
                        "JOIN po.assignedUser u " +
                        "WHERE po.isDeleted = false " +
                        "AND po.line.id = :lineId " +
                        "ORDER BY " +
                        "    CASE po.status " +
                        "        WHEN 'IN_PROGRESS' THEN 1 " +
                        "        WHEN 'COMPLETED' THEN 2 " +
                        "        WHEN 'RELEASED' THEN 3 " +
                        "        WHEN 'DRAFT' THEN 4 " +
                        "        ELSE 5 " +
                        "    END, " +
                        "    po.startDate DESC NULLS LAST, " +
                        "    po.createdAt DESC")
        List<Object[]> findOrdersByLineId(@Param("lineId") Long lineId);

        @Modifying
        @Transactional
        @Query("UPDATE ProductionOrder po SET po.isDeleted = true WHERE po.id = :id")
        void softDeleteById(@Param("id") Long id);
}