package com.example.demo.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.interf.ProductionService;
import com.example.demo.util.GenerateCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionServiceImpl implements ProductionService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    

    @Override
    public PageResponse<ProductResponse> getAllProductions(Pageable pageable, Optional<String> name, Optional<Long> categoryId, Optional<Boolean> isDeleted, Optional<String> product_code) {
        
        Page<Product> products = productRepository.findAllWithFilters(
            pageable, 
            name.orElse(null), 
            categoryId.orElse(null), 
          
            isDeleted.orElse(null), 
            product_code.orElse(null));

         // Convert Page<Product> sang Page<ProductResponse>
        Page<ProductResponse> responsePage = products.map(productMapper::toResponse);
        
        // Convert sang PagedResponse
        return PageResponse.fromPage(responsePage);
    }

    @Override
    public ProductResponse getProductionById(Long id) {
       
        //kiem tra san pham co ton tai va chua bi xoa
        Product productOpt = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Product not found or has been deleted",id));

        return productMapper.toResponse(productOpt);
    }

    @Override
    @Transactional
    public ProductResponse createProduction(CreateProductRequest request) {
        
        Product product = productMapper.toEntity(request);

        // tạo mã code
        String generatedCode = GenerateCode.generateProductCode();
        product.setProductCode(generatedCode);

        //kiểm tra category có tồn tại hay không, nếu không thì ném ra exception
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found", request.getCategoryId()));
        product.setCategory(category);

        product = productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduction(Long id, UpdateProductRequest request) {
        
        //kiểm tra sản phẩm có tồn tại và chưa bị xóa
        Product product = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Product not found or has been deleted", id));

        //update thông tin sản phẩm
        productMapper.updateEntityFromRequest(request, product);

        
        productRepository.save(product);
        
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduction(Long id) {
        
        //kiểm tra sản phẩm có tồn tại và chưa bị xóa
        Product product = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Product not found or has been deleted", id));

        //đánh dấu sản phẩm là đã xóa
        productRepository.softDeleteById(id);

        return;
        
    }

}