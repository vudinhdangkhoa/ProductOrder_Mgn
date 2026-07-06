package com.example.demo.repository;

import com.example.demo.entity.Category;
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
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndIsDeletedFalse(Long id);

    Optional<Category> findByCategoryCodeAndIsDeletedFalse(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);

    Page<Category> findAllByIsDeletedFalse(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteById(@Param("id") Long id);
}