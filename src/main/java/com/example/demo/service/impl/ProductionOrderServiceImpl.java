package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.CreateProductionOrderRequest;
import com.example.demo.dto.request.UpdateProductionOrderRequest;
import com.example.demo.dto.response.AuditLogPOResponse;
import com.example.demo.dto.response.ProductionOrderResponse;
import com.example.demo.entity.Line;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductionOrder;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.ProductionOrderStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductionOrderMapper;
import com.example.demo.repository.LineRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductionOrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interf.AuditLogPOService;
import com.example.demo.service.interf.ProductionOrderService;
import com.example.demo.util.DateUtils;
import com.example.demo.util.GenerateCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionOrderServiceImpl implements ProductionOrderService {

    private final ProductionOrderRepository orderRepository;
    private final ProductionOrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AuditLogPOService auditLogPOService;
    private final ProductRepository productRepository;
    private final LineRepository lineRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional

    public ProductionOrderResponse createOrder(CreateProductionOrderRequest request, Long userId) {

        // check start date and end date
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new BusinessException("INVALID_DATE_RANGE", "ngay bat dau phai nho hon ngay ket thuc");
            }
        }

        // Check for time conflicts with existing production orders for the same line
        List<ProductionOrder> existingOrders = orderRepository.findAllByLineIdAndIsDeletedFalse(request.getLineId());
        if (DateUtils.isTimeConflict(existingOrders, request)) {
            throw new BusinessException("TIME_CONFLICT",
                    "thoi gian bat dau va ket thuc cua lenh san xuat bi trung voi lenh san xuat khac tren cung mot line");
        }

        ProductionOrder order = orderMapper.toEntity(request);

        // Fetch and set related entities
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
        order.setProduct(product);
        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new ResourceNotFoundException("Line", request.getLineId()));

        order.setLine(line);
        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssignedUserId()));
        order.setAssignedUser(assignedUser);

        String orderCode = GenerateCode.generateProductOrderCode();
        order.setOrderCode(orderCode);

        ProductionOrder saved = orderRepository.save(order);
        // Create audit log entry for the new order
        AuditLogPOResponse auditLogResponse = AuditLogPOResponse.builder()
                .productOrderId(saved.getId())
                .action(ProductionOrderStatus.DRAFT)
                .userId(userId) // Assuming userId is part of the request
                .createdAt(java.time.LocalDateTime.now())
                .build();

        auditLogPOService.saveAuditLog(auditLogResponse);

        // Bắn event để đồng bộ realtime sau khi commit
        // eventPublisher.publishEvent(new ProductionOrderEvent(saved, "CREATED"));

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<AuditLogPOResponse> getOrderById(Long id) {
        return auditLogPOService.getAllByProductionOrderId(id);

    }

    // @Override
    // public Page<ProductionOrderResponse> searchOrders(ProductionOrderStatus
    // status, Long lineId,
    // Long assignedUserId, LocalDate startDate,
    // LocalDate endDate, String keyword, Pageable pageable) {
    // return orderRepository.searchOrders(status, lineId, assignedUserId,
    // startDate, endDate, keyword, pageable)
    // .map(orderMapper::toResponse);
    // }

    @Override
    @Transactional

    public ProductionOrderResponse updateOrder(Long id, UpdateProductionOrderRequest request, Long userId) {
        log.info("Updating production order id: {}", id);
        ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        // Rule 3: Không sửa lệnh đã Released (Ví dụ logic nghiệp vụ)
        if (order.getStatus() == ProductionOrderStatus.RELEASED
                || order.getStatus() == ProductionOrderStatus.COMPLETED
                || order.getStatus() == ProductionOrderStatus.CANCELLED
                || order.getStatus() == ProductionOrderStatus.DELETED
                || order.getStatus() == ProductionOrderStatus.IN_PROGRESS) {
            throw new BusinessException("INVALID_STATUS",
                    "Cannot update released, completed, cancelled or deleted order");
        }

        // Check for time conflicts with existing production orders for the same line
        // Check for time conflicts with existing production orders for the same line
        List<ProductionOrder> existingOrders = orderRepository.findAllByLineIdAndIsDeletedFalse(order.getLine().getId());
        CreateProductionOrderRequest newOrderRequest = new CreateProductionOrderRequest();
        newOrderRequest.setStartDate(request.getStartDate());
        newOrderRequest.setEndDate(request.getEndDate());

        if (DateUtils.isTimeConflict(existingOrders, newOrderRequest)) {
            throw new BusinessException("TIME_CONFLICT",
                    "thoi gian bat dau va ket thuc cua lenh san xuat bi trung voi lenh san xuat khac tren cung mot line");
        }

        orderMapper.updateEntityFromRequest(request, order);
        ProductionOrder updated = orderRepository.save(order);

        // update audit log
        

        // eventPublisher.publishEvent(new ProductionOrderEvent(updated, "UPDATED"));

        return orderMapper.toResponse(updated);
    }

    @Override
    // Chỉ cho phép người dùng có quyền ORDER_DELETE truy cập
    @Transactional
    public void deleteOrder(Long id, Long userId) {
        log.info("Soft deleting production order id: {}", id);
         ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        if (order.getStatus() != ProductionOrderStatus.DRAFT) {
            throw new BusinessException("INVALID_STATUS", "Only orders in DRAFT status can be deleted");
        }

        orderRepository.softDeleteById(id);

        // update audit log
        AuditLogPOResponse auditLogResponse = AuditLogPOResponse.builder()
                .productOrderId(id)
                .action(ProductionOrderStatus.DELETED)
                .userId(userId)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        auditLogPOService.saveAuditLog(auditLogResponse);
    }

    @Override
    @Transactional
    public Page<ProductionOrderResponse> getOrder(Pageable pageable, Optional<ProductionOrderStatus> status,
            Optional<Long> lineId, Optional<String> assignedUserUserName, Optional<LocalDate> startDate,
            Optional<LocalDate> endDate, Optional<String> POCode) {
            
            User user = userRepository.findByUsernameAndIsDeletedFalse(assignedUserUserName.orElse(null))
                .orElse(null);
        
        return orderRepository.searchOrders(
                status.orElse(null),
                lineId.orElse(null),
                user != null ? user.getId() : null,
                startDate.orElse(null),
                endDate.orElse(null),
                POCode.orElse(null),
                pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductionOrderResponse releaseOrder(Long id, String userName) {
        log.info("Releasing production order id: {}", id);
        ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        if (order.getStatus() != ProductionOrderStatus.DRAFT) {
            throw new BusinessException("INVALID_STATUS", "Only orders in CREATED status can be released");
        }

        order.setStatus(ProductionOrderStatus.RELEASED);
        ProductionOrder updated = orderRepository.save(order);
       
        User user = userRepository.findByUsernameAndIsDeletedFalse(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user", 0));

        // update audit log
        AuditLogPOResponse auditLogResponse = AuditLogPOResponse.builder()
                .productOrderId(updated.getId())
                .action(ProductionOrderStatus.RELEASED)
                .userId(user.getId())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        auditLogPOService.saveAuditLog(auditLogResponse);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductionOrderResponse completeOrder(Long id, Long userId, ProductionOrderStatus request) {
        log.info("Updating production order id: {}", id);
        ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        // Rule 3: Không sửa lệnh đã Released (Ví dụ logic nghiệp vụ)
        if (order.getStatus() == ProductionOrderStatus.DRAFT
                || order.getStatus() == ProductionOrderStatus.COMPLETED
                || order.getStatus() == ProductionOrderStatus.CANCELLED
                || order.getStatus() == ProductionOrderStatus.DELETED) {
            throw new BusinessException("INVALID_STATUS",
                    "Cannot update released, completed, cancelled or deleted order");
        }

        order.setStatus(request);
        ProductionOrder updated = orderRepository.save(order);

        // update audit log
        AuditLogPOResponse auditLogResponse = AuditLogPOResponse.builder()
                .productOrderId(updated.getId())
                .action(request)
                .userId(userId)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        auditLogPOService.saveAuditLog(auditLogResponse);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductionOrderResponse cancelOrder(Long id, Long userId, String reason) {
        log.info("Cancelling production order id: {}", id);
        ProductionOrder order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionOrder", id));

        if (order.getStatus() == ProductionOrderStatus.COMPLETED
                || order.getStatus() == ProductionOrderStatus.CANCELLED
                || order.getStatus() == ProductionOrderStatus.DELETED
                || order.getStatus() == ProductionOrderStatus.RELEASED
                || order.getStatus() == ProductionOrderStatus.DRAFT) {
            throw new BusinessException("Cannot cancel completed or already cancelled orders", "cannot_cancel_completed_or_cancelled");
        }

        order.setStatus(ProductionOrderStatus.CANCELLED);
        order.setReason(reason); // Set the cancellation reason
        ProductionOrder updated = orderRepository.save(order);

        // update audit log
        AuditLogPOResponse auditLogResponse = AuditLogPOResponse.builder()
                .productOrderId(updated.getId())
                .action(ProductionOrderStatus.CANCELLED)
                .userId(userId)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        auditLogPOService.saveAuditLog(auditLogResponse);

        return orderMapper.toResponse(updated);
    }

    @Override
    public Map<String, List<ProductionOrderResponse>> listConflictOrdersDRAFT(Long lineId) {

        // Lấy tất cả các lệnh sản xuất có trạng thái DRAFT cho lineId
        List<ProductionOrder> draftOrders = orderRepository.findAllDraftByLineIdAndIsDeletedFalse(lineId);

        // Chuyển đổi sang DTO
        List<ProductionOrderResponse> draftResponses = draftOrders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());

        // Nhóm các lệnh bị trùng nhau theo khoảng thời gian
        List<List<ProductionOrderResponse>> groups = new ArrayList<>();

        for (ProductionOrderResponse order : draftResponses) {
            List<List<ProductionOrderResponse>> matchedGroups = new ArrayList<>();

            // Tìm xem order này có trùng với nhóm nào đã tạo trước đó không
            for (List<ProductionOrderResponse> group : groups) {
                boolean overlapsWithGroup = group.stream().anyMatch(gMember -> isOverlapping(order, gMember));
                if (overlapsWithGroup) {
                    matchedGroups.add(group);
                }
            }

            if (matchedGroups.isEmpty()) {
                // Không trùng với bất kỳ nhóm nào trước đó -> tạo một nhóm mới

                List<ProductionOrderResponse> newGroup = new ArrayList<>();
                newGroup.add(order);
                groups.add(newGroup);
            } else if (matchedGroups.size() == 1) {
                // Trùng với đúng 1 nhóm -> thêm vào nhóm đó
                matchedGroups.get(0).add(order);
            } else {
                // Trùng với nhiều nhóm (lệnh này là cầu nối 2 nhóm đã tách biệt trước đó lại)
                // gộp tất cả các nhóm trùng này lại làm một nhóm lớn duy nhất
                List<ProductionOrderResponse> mergedGroup = matchedGroups.get(0);
                mergedGroup.add(order);
                for (int k = 1; k < matchedGroups.size(); k++) {
                    mergedGroup.addAll(matchedGroups.get(k));
                    groups.remove(matchedGroups.get(k)); // Xóa nhóm cũ đã bị gộp đi
                }
            }
        }

        // 4. Đưa kết quả vào Map theo format "Nhóm trùng 1", "Nhóm trùng 2"
        Map<String, List<ProductionOrderResponse>> conflictGroupsMap = new LinkedHashMap<>();
        int groupCounter = 1;

        for (List<ProductionOrderResponse> group : groups) {
            // Chỉ giữ lại những nhóm có từ 2 lệnh trở lên (thực sự có xung đột lẫn nhau)
            if (group.size() > 1) {
                String groupKey = "Lệnh trùng " + groupCounter++;
                conflictGroupsMap.put(groupKey, group);
            }
        }

        return conflictGroupsMap;
    }

    private boolean isOverlapping(ProductionOrderResponse o1, ProductionOrderResponse o2) {
        return o1.getStartDate().isBefore(o2.getEndDate()) &&
                o1.getEndDate().isAfter(o2.getStartDate());
    }

}