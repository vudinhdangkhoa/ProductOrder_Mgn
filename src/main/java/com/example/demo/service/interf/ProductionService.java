package com.example.demo.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.dto.response.ProductResponse;


public interface ProductionService {

    Page<ProductResponse> getAllProductions(Pageable pageable, Optional<String> name, Optional<Long> categoryId, Optional<Boolean> isActive, Optional<Boolean> isDeleted, Optional<String> product_code);

    ProductResponse getProductionById(Long id);

    ProductResponse createProduction(CreateProductRequest request);

    ProductResponse updateProduction(Long id, UpdateProductRequest request);

    void deleteProduction(Long id);
}

