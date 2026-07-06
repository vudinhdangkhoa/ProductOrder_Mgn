package com.example.demo.repository;

import com.example.demo.entity.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    Optional<Line> findByIdAndIsDeletedFalse(Long id);

    Optional<Line> findByLineCodeAndIsDeletedFalse(String lineCode);

    boolean existsByLineCode(String lineCode);

    Page<Line> findAllByIsDeletedFalse(Pageable pageable);

    // Lấy danh sách các dây chuyền đang hoạt động (Phục vụ dropdown tạo Lệnh sản xuất)
    Page<Line> findAllByIsActiveTrueAndIsDeletedFalse(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Line l SET l.isDeleted = true WHERE l.id = :id")
    void softDeleteById(@Param("id") Long id);
}