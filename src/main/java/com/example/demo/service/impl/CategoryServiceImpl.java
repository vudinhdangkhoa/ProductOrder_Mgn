package com.example.demo.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateCategoryRequest;
import com.example.demo.dto.request.UpdateCategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Category;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.interf.CategoryService;
import com.example.demo.util.GenerateCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        

        List<Category> categories = categoryRepository.findAllByIsDeletedFalse();
               
        return PageResponse.<CategoryResponse>builder()
                .content(categories.stream().map(categoryMapper::toResponse).toList())
                .build();
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest categoryResponse) {
        
        //kiểm tra xem category name đã tồn tại chưa
        Category existingCategory = categoryRepository.findByNameAndIsDeletedFalse(categoryResponse.getName())
                .orElse(null);

        if (existingCategory != null) {
            throw new DuplicateResourceException("Category name already exists: " + categoryResponse.getName());
        }


    Category category = Objects.requireNonNull(categoryMapper.toEntity(categoryResponse));
    category.setCategoryCode(GenerateCode.generateCategoryCode());
    category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryResponse) {
        
        //kiểm tra xem category name đã tồn tại chưa
        Category existingCategory = categoryRepository.findByNameAndIsDeletedFalse(categoryResponse.getName())
                .orElse(null);

        if (existingCategory != null && !existingCategory.getId().equals(id)) {
            throw new DuplicateResourceException("Category name already exists: " + categoryResponse.getName());
        }

        //kiểm tra xe category đã tồn tại và có bị xóa chưa
        Category categoryToUpdate = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or has been deleted", id));
        
        // Cập nhật thông tin category
        categoryMapper.updateEntityFromRequest(categoryResponse, categoryToUpdate);
        Category savedCategory = categoryRepository.save(Objects.requireNonNull(categoryToUpdate));

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        //kiểm tra xe category đã tồn tại và có bị xóa chưa
        categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or has been deleted", id));
        
        // Thực hiện xóa mềm
        categoryRepository.softDeleteById(id);

    }

}
