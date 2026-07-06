package com.company.project.service.event;

import com.company.project.dto.response.ProductionOrderResponse;
import com.company.project.mapper.ProductionOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ProductionOrderMapper orderMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductionOrderEvent(ProductionOrderEvent event) {
        log.info("Processing realtime event for order: {}", event.getProductionOrder().getOrderCode());
        
        ProductionOrderResponse dto = orderMapper.toResponse(event.getProductionOrder());
        
        // Bạn có thể wrap lại trong một ApiResponse để đồng bộ với controller response
        var payload = ApiResponse.success(dto, "Order " + event.getAction());
        
        // Push qua WebSocket topic
        messagingTemplate.convertAndSend("/topic/production-orders", payload);
    }
}