package com.example.demo.service.interf;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;


public interface ProductionService {

    PageResponse<ProductResponse> getAllProductions(Pageable pageable, Optional<String> name, Optional<Long> categoryId, Optional<Boolean> isDeleted, Optional<String> product_code);

    ProductResponse getProductionById(Long id);

    ProductResponse createProduction(CreateProductRequest request);

    ProductResponse updateProduction(Long id, UpdateProductRequest request);

    List<ProductResponse> getAllProductionsWithoutPagination();

    void deleteProduction(Long id);
}

