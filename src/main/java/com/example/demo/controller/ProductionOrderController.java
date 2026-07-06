package com.example.demo.controller;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.enums.ProductionOrderStatus;
import com.example.demo.service.ProductionOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Production Order", description = "Lệnh sản xuất API")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @PostMapping
    @Operation(summary = "Tạo lệnh sản xuất mới")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> create(
            @Valid @RequestBody CreateProductionOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productionOrderService.createOrder(request), "Lệnh sản xuất đã được tạo"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết lệnh sản xuất")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productionOrderService.getOrderById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm và lọc lệnh sản xuất")
    public ResponseEntity<ApiResponse<PageResponse<ProductionOrderResponse>>> search(
            @RequestParam(required = false) ProductionOrderStatus status,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<ProductionOrderResponse> data = productionOrderService.searchOrders(
                status, lineId, assignedUserId, startDate, endDate, keyword, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(data)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật lệnh sản xuất (Update 1 phần)")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductionOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productionOrderService.updateOrder(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa lệnh sản xuất (Soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productionOrderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Lệnh sản xuất đã bị hủy"));
    }
}