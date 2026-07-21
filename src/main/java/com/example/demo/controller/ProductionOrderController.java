package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AuditLogPOResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.enums.ProductionOrderStatus;
import com.example.demo.service.interf.ProductionOrderService;
import com.example.demo.service.interf.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Production Order", description = "Lệnh sản xuất API")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ORDER_CREATE')") // Chỉ cho phép người dùng có quyền ORDER_CREATE truy cập
    @Operation(summary = "Tạo lệnh sản xuất mới")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> create( @Valid @RequestBody CreateProductionOrderRequest request,
                                                                         Authentication authentication) {
        UserResponse user = userService.getUserByUsernameAndIsDeletedFalse(authentication.getName());
        ProductionOrderResponse response = productionOrderService.createOrder(request, user.getId());
        return ResponseEntity.ok(ApiResponse.<ProductionOrderResponse>builder()
                .success(true)
                .data(response)
                .message("Lệnh sản xuất đã được tạo thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')") // Chỉ cho phép người dùng có quyền ORDER_VIEW truy cập
    @Operation(summary = "Lấy chi tiết lệnh sản xuất")
    public ResponseEntity<ApiResponse<List<AuditLogPOResponse>>> getById(@PathVariable Long id) {
        List<AuditLogPOResponse> response = productionOrderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.<List<AuditLogPOResponse>>builder()
                .success(true)
                .data(response)
                .message("Lấy chi tiết lệnh sản xuất thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
        
    }

    

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')") // Chỉ cho phép người dùng có quyền ORDER_UPDATE truy cập
    @Operation(summary = "Cập nhật lệnh sản xuất (Update 1 phần)")
    public ResponseEntity<ApiResponse<ProductionOrderResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductionOrderRequest request,
            Authentication authentication
        ) {
        UserResponse user = userService.getUserByUsernameAndIsDeletedFalse(authentication.getName());
        return ResponseEntity.ok(ApiResponse.<ProductionOrderResponse>builder()
                .success(true)
                .data(productionOrderService.updateOrder(id, request, user.getId()))
                .message("Lệnh sản xuất đã được cập nhật thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_DELETE')") // Chỉ cho phép người dùng có quyền ORDER_DELETE truy cập
    @Operation(summary = "Xóa lệnh sản xuất (Soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication authentication) {
        UserResponse user = userService.getUserByUsernameAndIsDeletedFalse(authentication.getName());
        productionOrderService.deleteOrder(id, user.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Lệnh sản xuất đã được xóa thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_VIEW')") // Chỉ cho phép người dùng có quyền ORDER_VIEW truy cập
    @Operation(summary = "Lấy danh sách lệnh sản xuất với phân trang và lọc")
    public ResponseEntity<ApiResponse<PageResponse<ProductionOrderResponse>>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) ProductionOrderStatus status,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) String assignedUserUserName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String POCode
        ) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<ProductionOrderResponse> data = productionOrderService.getOrder(pageable, Optional.ofNullable(status), Optional.ofNullable(lineId), Optional.ofNullable(assignedUserUserName), Optional.ofNullable(startDate), Optional.ofNullable(endDate), Optional.ofNullable(POCode));
        
        return ResponseEntity.ok(ApiResponse.<PageResponse<ProductionOrderResponse>>builder()
                .success(true)
                .data(PageResponse.fromPage(data))
                .message("Lấy danh sách lệnh sản xuất thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @GetMapping("/Conflict-DRAFT")
    @PreAuthorize("hasAuthority('ORDER_VIEW')") // Chỉ cho phép người dùng có quyền ORDER_VIEW truy cập
    @Operation(summary = "Lấy danh sách lệnh sản xuất trùng lặp DRAFT theo lineId")
    public ResponseEntity<ApiResponse<Map<String, List<ProductionOrderResponse>>>> ListConflictOrders(@RequestParam Long lineId) {

        Map<String, List<ProductionOrderResponse>> conflictOrders = productionOrderService.listConflictOrdersDRAFT(lineId);

        return ResponseEntity.ok(ApiResponse.<Map<String, List<ProductionOrderResponse>>>builder()
                .success(true)
                .data(conflictOrders)
                .message("Lấy danh sách lệnh sản xuất trùng lặp thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }
    
   
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ORDER_PROCESS')") // Chỉ cho phép người dùng có quyền ORDER_UPDATE truy cập
    @Operation(summary = "Cập nhật trạng thái lệnh sản xuất")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(@PathVariable Long id,Authentication authentication, @RequestParam ProductionOrderStatus entity) {
        //TODO: process PUT request
         UserResponse user = userService.getUserByUsernameAndIsDeletedFalse(authentication.getName());
        productionOrderService.completeOrder(id, user.getId(), entity);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Cập nhật trạng thái lệnh sản xuất thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ORDER_CANCEL')") // Chỉ cho phép người dùng có quyền ORDER_CANCEL truy cập
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id, @RequestParam String reason, Authentication authentication) {
        //TODO: process PUT request
        UserResponse user = userService.getUserByUsernameAndIsDeletedFalse(authentication.getName());
        productionOrderService.cancelOrder(id, user.getId(), reason);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Hủy lệnh sản xuất thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/release")
    @PreAuthorize("hasAuthority('ORDER_RELEASE')") // Chỉ cho phép người dùng
    @Operation(summary = "Phát hành lệnh sản xuất")
    public ResponseEntity<ApiResponse<Void>> releaseOrder(@PathVariable Long id, @RequestParam String userName) {
        productionOrderService.releaseOrder(id, userName);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Phát hành lệnh sản xuất thành công")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }
}