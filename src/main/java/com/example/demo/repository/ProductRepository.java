package com.example.demo.repository;

import com.example.demo.entity.Product;
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
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findByProductCodeAndIsDeletedFalse(String productCode);

    boolean existsByProductCode(String productCode);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    // Tìm kiếm sản phẩm theo danh mục (Phục vụ bộ lọc)
    Page<Product> findAllByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "WHERE p.isDeleted = false " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.id = :id")
    void softDeleteById(@Param("id") Long id);
}