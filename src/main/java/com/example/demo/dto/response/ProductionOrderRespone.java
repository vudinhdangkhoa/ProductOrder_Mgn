package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderResponse {
    private Long id;
    private String orderCode;
    
    // Flatten fields for Product
    private Long productId;
    private String productName;
    
    private Integer quantity;
    
    // Flatten fields for Line
    private Long lineId;
    private String lineName;
    
    // Flatten fields for Assigned User
    private Long assignedUserId;
    private String assignedUserName;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // Enum được parse thành String
    private LocalDateTime createdAt;
}