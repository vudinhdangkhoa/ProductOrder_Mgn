package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.entity.ProductionOrder;
import com.example.demo.repository.ProductionOrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DateUtils {

    private final ProductionOrderRepository productionOrderRepository;

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    //Check xung đột thời gian PO
    public static Boolean isTimeConflict( List<ProductionOrder> existingOrders,CreateProductionOrderRequest newOrder) {
       
        //tìm tất cả các PO của lineId trùng với newOrder
        
        for (ProductionOrder productionOrder : existingOrders) {
            if(productionOrder.getStartDate().isBefore(newOrder.getEndDate()) &&
               productionOrder.getEndDate().isAfter(newOrder.getStartDate())) {
                return true; // Có xung đột thời gian
            }
        }

        return false; // Implement the logic to check for time conflicts with existing production orders
    }

   
}
