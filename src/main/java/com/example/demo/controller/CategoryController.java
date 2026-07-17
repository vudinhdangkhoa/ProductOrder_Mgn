package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateCategoryRequest;
import com.example.demo.dto.request.UpdateCategoryRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.interf.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "API quản lý danh mục sản phẩm")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories( Pageable pageable) {
    log.info("REST request to get all categories");

    PageResponse<CategoryResponse> categories = categoryService.getAllCategories(pageable);

    return ResponseEntity.ok(
        ApiResponse.<PageResponse<CategoryResponse>>builder()
            .success(true)
            .data(categories)
            .message(categories.isEmpty() ? "Không có danh mục nào" : "Lấy danh sách danh mục thành công")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
        @Valid @RequestBody CreateCategoryRequest request) {
    

    CategoryResponse response = categoryService.createCategory(request);

    return ResponseEntity.ok(
        ApiResponse.<CategoryResponse>builder()
            .success(true)
            .data(response)
            .message("Tạo danh mục thành công")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCategoryRequest request) {
    log.info("REST request to update category: {}", id);

    CategoryResponse response = categoryService.updateCategory(id, request);

    return ResponseEntity.ok(
        ApiResponse.<CategoryResponse>builder()
            .success(true)
            .data(response)
            .message("Cập nhật danh mục thành công")
            .timestamp(LocalDateTime.now())
            .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
    log.info("REST request to delete category: {}", id);

    categoryService.deleteCategory(id);

    return ResponseEntity.ok(
        ApiResponse.<String>builder()
            .success(true)
            .message("Xóa danh mục thành công")
            .timestamp(LocalDateTime.now())
            .build());
    }

}
