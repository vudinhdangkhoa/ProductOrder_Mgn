package com.example.demo.dto.request;

import com.example.demo.entity.enums.ProductionOrderStatus;

import jakarta.validation.constraints.NotEmpty;
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
public class UpdateProductionOrderStatus {

    @NotEmpty(message = "Trạng thái không được để trống")
    private ProductionOrderStatus status;

}
