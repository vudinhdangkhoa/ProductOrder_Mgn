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
public class LineResponse {
    private Long id;
    private String lineCode;
    private String name;
    private String description;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}