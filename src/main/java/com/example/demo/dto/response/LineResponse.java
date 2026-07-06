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
public class LineResponse {
    private Long id;
    private String lineCode;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}