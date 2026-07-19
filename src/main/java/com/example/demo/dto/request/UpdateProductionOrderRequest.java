package com.example.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductionOrderRequest {

    @NotBlank(message = "số lượng không được để trống")
    
    private Long quantity;

    @NotBlank(message = "ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotBlank(message = "ngày kết thúc không được để trống")
    private LocalDate endDate;

   
}