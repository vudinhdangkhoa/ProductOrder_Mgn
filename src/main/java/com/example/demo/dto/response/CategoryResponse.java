package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String productCode;
    private String name;
    private String description;
    private Boolean isActive;
    
    // Flatten fields
    private Long categoryId;
    private String categoryName;
    
    private LocalDateTime createdAt;
}