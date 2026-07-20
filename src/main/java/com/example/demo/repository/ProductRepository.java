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

import com.example.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findByProductCodeAndIsDeletedFalse(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findAllByIsDeletedFalse();

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    // Tìm kiếm sản phẩm theo danh mục (Phục vụ bộ lọc)
    Page<Product> findAllByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "WHERE p.isDeleted = false " +
           "AND (:category_id IS NULL OR p.category.id = :category_id) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("category_id") Long categoryId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "WHERE (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:category_id IS NULL OR p.category.id = :category_id) " +
           "AND (COALESCE(:product_code, '') = '' OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :product_code, '%'))) " +
           "AND (:is_deleted IS NULL OR p.isDeleted = :is_deleted)")
    Page<Product> findAllWithFilters(
            Pageable pageable,
            @Param("name") String name,
            @Param("category_id") Long categoryId,
            @Param("is_deleted") Boolean is_deleted,
            @Param("product_code") String productCode
    );

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.id = :id")
    void softDeleteById(@Param("id") Long id);
}