package com.example.demo.dto.response;

import com.example.demo.entity.enums.ProductionOrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LineStatusCountResponse {

    private String lineName;
    private ProductionOrderStatus status;
    private Long count;

}
