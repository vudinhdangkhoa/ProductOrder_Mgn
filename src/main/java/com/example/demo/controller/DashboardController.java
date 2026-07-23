package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.LineCurrentOrderResponse;
import com.example.demo.entity.enums.ProductionOrderStatus;
import com.example.demo.service.interf.ProductionOrderService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "API quản lý bảng điều khiển")
public class DashboardController {

    private final ProductionOrderService productionOrderService;

    @GetMapping("stats")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {

        Map<String, Long> stats = Map.of(
                ProductionOrderStatus.RELEASED.toString(), productionOrderService.countOrdersByStatusByMonth(ProductionOrderStatus.RELEASED, java.time.LocalDate.now()),
                ProductionOrderStatus.IN_PROGRESS.toString(), productionOrderService.countOrdersByStatusByMonth(ProductionOrderStatus.IN_PROGRESS, java.time.LocalDate.now()),
                ProductionOrderStatus.COMPLETED.toString(), productionOrderService.countOrdersByStatusByMonth(ProductionOrderStatus.COMPLETED, java.time.LocalDate.now()),
                ProductionOrderStatus.CANCELLED.toString(), productionOrderService.countOrdersByStatusByMonth(ProductionOrderStatus.CANCELLED, java.time.LocalDate.now()),
                ProductionOrderStatus.DRAFT.toString(), productionOrderService.countOrdersByStatusByMonth(ProductionOrderStatus.DRAFT, java.time.LocalDate.now())
        );
        return ResponseEntity.ok(ApiResponse.<Map<String,Long>>builder()
                .success(true)
                .data(stats)
                .message("Get dashboard stats successfully")
                .build());
    }
    
    @GetMapping("chart")
     @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
     public ResponseEntity<ApiResponse<Map<String,Map<String,Long>>>> getChartData() {
         
        Map<String,Map<String,Long>> chartData = productionOrderService.countOrdersByLineAndStatus(java.time.LocalDate.now());

         return ResponseEntity.ok(ApiResponse.<Map<String,Map<String,Long>>>builder()
                 .success(true)
                 .data(chartData)
                 .message("Get dashboard chart data successfully")
                 .build());
     }


     @GetMapping("LineStats")
     public ResponseEntity<ApiResponse<List<LineCurrentOrderResponse>>> getLineStats() {

                List<LineCurrentOrderResponse> lineStats = productionOrderService.getCurrentOrdersForAllLines();
        
                return ResponseEntity.ok(ApiResponse.<List<LineCurrentOrderResponse>>builder()
                        .success(true)
                        .data(lineStats)
                        .message("Get line stats successfully")
                        .build());

        
     }
     

}
