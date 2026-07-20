package com.example.demo.repository;

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

import com.example.demo.entity.Line;

@Repository
@Transactional(readOnly = true)
public interface LineRepository extends JpaRepository<Line, Long> {

    List<Line> findAllByIsDeletedFalse();

    Optional<Line> findByIdAndIsDeletedFalse(Long id);

    Optional<Line> findByLineCodeAndIsDeletedFalse(String lineCode);

    boolean existsByLineCode(String lineCode);


    @Transactional
    @Query("SELECT l FROM Line l WHERE l.isDeleted = false AND (:lineCode IS NULL OR l.lineCode LIKE %:lineCode%) AND (:name IS NULL OR l.name LIKE %:name%)")
    Page<Line> findAllByIsDeletedFalse(Pageable pageable, Optional<String> lineCode, Optional<String> name);


    @Modifying
    @Transactional
    @Query("UPDATE Line l SET l.isDeleted = true WHERE l.id = :id")
    void softDeleteById(@Param("id") Long id);
}