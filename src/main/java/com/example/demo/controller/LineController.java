package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.LineResponse;
import com.example.demo.dto.response.PageResponse;
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
    


}
