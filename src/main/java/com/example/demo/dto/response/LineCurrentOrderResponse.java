package com.example.demo.dto.response;

import java.time.LocalDate;

import com.example.demo.entity.enums.ProductionOrderStatus;

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
public class LineCurrentOrderResponse {

    private String lineName;
     private ProductionOrderStatus status;
      private String orderCode;
    private LocalDate startDate;
    private LocalDate endDate;
   
   

    private int quantity;
    private String productName;
    private String assignedUserFullName;
}
