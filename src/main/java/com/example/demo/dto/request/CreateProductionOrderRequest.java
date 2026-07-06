package com.example.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductionOrderRequest {

    @NotBlank(message = "Mã lệnh không được để trống")
    @Size(max = 20, message = "Mã lệnh tối đa 20 ký tự")
    private String orderCode;

    @NotNull(message = "Sản phẩm là bắt buộc")
    private Long productId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Dây chuyền là bắt buộc")
    private Long lineId;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDate endDate;
}
