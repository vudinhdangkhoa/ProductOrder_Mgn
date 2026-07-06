package com.example.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductionOrderRequest {

    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private Long lineId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 20, message = "Trạng thái tối đa 20 ký tự")
    private String status;
}