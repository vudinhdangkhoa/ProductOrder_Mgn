package com.example.demo.service.interf;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.CreateCategoryRequest;
import com.example.demo.dto.request.UpdateCategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.PageResponse;



public interface CategoryService {
    
    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);

    CategoryResponse createCategory(CreateCategoryRequest categoryResponse);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryResponse);

    void deleteCategory(Long id);
}
