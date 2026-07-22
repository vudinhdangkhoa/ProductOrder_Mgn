package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateLineRequest;
import com.example.demo.dto.request.UpdateLineRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.service.interf.LineService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;





@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "LineController", description = "API quản lý day chuyen")

public class LineController {
    
    private final LineService lineService;


    @GetMapping
    @PreAuthorize("hasAuthority('LINE_VIEW')") // Chỉ cho phép người dùng có quyền LINE_VIEW truy cập
    public ResponseEntity<ApiResponse<PageResponse<LineResponse>>> GetAllLine(Pageable pageable, @RequestParam(required = false) Optional<String> name, @RequestParam(required = false) Optional<String> code) {
        
        PageResponse<LineResponse> lines = lineService.getAllLines(pageable, name, code);

        return ResponseEntity.ok(ApiResponse.<PageResponse<LineResponse>>builder()
        .success(true)
                .data(lines)
                .message("Get all lines successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<LineResponse>>> getMethodName() {
        List<LineResponse> lines = lineService.getAllLinesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.<List<LineResponse>>builder()
                .success(true)
                .data(lines)
                .message("Get all lines successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('LINE_MANAGE')") // Chỉ cho phép người dùng có quyền LINE_MAN
    public ResponseEntity<ApiResponse<LineResponse>> postMethodName(@RequestBody CreateLineRequest entity) {
        //TODO: process POST request
        LineResponse savedEntity = lineService.createLine(entity);
        return ResponseEntity.ok(ApiResponse.<LineResponse>builder()
                .success(true)
                .data(savedEntity)
                .message("Line created successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LINE_MANAGE')") // Chỉ cho phép người dùng có quyền LINE_MANAGE truy cập
    public ResponseEntity<ApiResponse<LineResponse>> putMethodName(@PathVariable Long id, @RequestBody UpdateLineRequest entity) {
        //TODO: process PUT request
        LineResponse updatedEntity = lineService.updateLine(id, entity);
        return ResponseEntity.ok(ApiResponse.<LineResponse>builder()
                .success(true)
                .data(updatedEntity)
                .message("Line updated successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LINE_VIEW')") // Chỉ cho phép người dùng có quyền LINE_VIEW truy cập
    public ResponseEntity<ApiResponse<PageResponse<ProductionOrderResponse>>> getMethodName(@PathVariable Long id, Pageable pageable) {
        PageResponse<ProductionOrderResponse> pageResponse = lineService.getPObyLineId(id, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<ProductionOrderResponse>>builder()
                .success(true)
                .data(pageResponse)
                .message("Line found successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }
    


}
