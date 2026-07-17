package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.service.interf.ProductionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;





@RestController
@RequestMapping("/api/production")
@Tag(name = "Production", description = "API quản lý sản phẩm")

@RequiredArgsConstructor
@Slf4j
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')") // Chỉ cho phép người dùng có quyền PRODUCT_VIEW truy cập
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getMethodName(
        Pageable pageable, 
        @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<Long> categoryId,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(required = false) Optional<String> product_code) {

        PageResponse<ProductResponse> products = productionService.getAllProductions(pageable, name, categoryId,  isDeleted, product_code);
        
        String message = products.isEmpty() 
            ? "Không có sản phẩm nào" 
            : "Lấy danh sách sản phẩm thành công";
        
        return ResponseEntity.ok(
            ApiResponse.<PageResponse<ProductResponse>>builder()
                .success(true)
                .data(products)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductionById(@PathVariable Long id) {
        ProductResponse product = productionService.getProductionById(id);
        return ResponseEntity.ok(
            ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Lấy thông tin sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @PostMapping
    
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        //TODO: process POST request
        ProductResponse product = productionService.createProduction(request);

        return ResponseEntity.ok(
            ApiResponse.<ProductResponse>builder()
                .success(true)
                .data(product)
                .message("Tạo sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @PutMapping("/{id}")
    
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        //TODO: process PUT request
        productionService.updateProduction(id, request);

        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .success(true)
                .message("Cập nhật sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @DeleteMapping("/{id}")
   
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
       
        productionService.deleteProduction(id);

        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .success(true)
                .message("Xóa sản phẩm thành công")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }


}
