package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRespone {

    private Long id;
    private String productCode;
    private String name;
    private String description;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;

}
