package com.example.demo.service.interf;

import java.util.List;

import com.example.demo.dto.request.CreateCategoryRequest;
import com.example.demo.dto.request.UpdateCategoryRequest;
import com.example.demo.dto.response.CategoryResponse;



public interface CategoryService {
    
    List<CategoryResponse> getAllCategories();

    CategoryResponse createCategory(CreateCategoryRequest categoryResponse);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryResponse);

    void deleteCategory(Long id);
}
