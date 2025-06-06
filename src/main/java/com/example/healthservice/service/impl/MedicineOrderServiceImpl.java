package com.example.healthservice.service.impl;

import com.example.healthservice.dto.MedicineOrderRequest;
import com.example.healthservice.dto.MedicineOrderResponse;
import com.example.healthservice.enums.OrderStatus;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.exception.ValidationException;
import com.example.healthservice.model.Medicine;
import com.example.healthservice.model.MedicineOrder;
import com.example.healthservice.repository.MedicineOrderRepository;
import com.example.healthservice.repository.MedicineRepository;
import com.example.healthservice.service.MedicineOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineOrderServiceImpl implements MedicineOrderService {

    private final MedicineOrderRepository orderRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public MedicineOrderResponse createOrder(String userId, MedicineOrderRequest request) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + request.getMedicineId()));

        // Validate medicine availability and expiry
        validateMedicineForOrder(medicine, request.getQuantity());

        // Create order
        MedicineOrder order = MedicineOrder.builder()
                .userId(userId)
                .medicineId(medicine.getId())
                .medicineName(medicine.getName())
                .quantity(request.getQuantity())
                .unitPrice(medicine.getPrice())
                .totalPrice(medicine.getPrice().multiply(new java.math.BigDecimal(request.getQuantity())))
                .deliveryAddress(request.getDeliveryAddress())
                .contactNumber(request.getContactNumber())
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Update medicine stock
        medicine.setStockQuantity(medicine.getStockQuantity() - request.getQuantity());
        medicineRepository.save(medicine);

        MedicineOrder savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public MedicineOrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new ValidationException("Cannot update status of cancelled or refunded order");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }

        MedicineOrder updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new ValidationException("Cannot cancel order that is already shipped or delivered");
        }

        // Restore medicine stock
        Medicine medicine = medicineRepository.findById(order.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + order.getMedicineId()));
        medicine.setStockQuantity(medicine.getStockQuantity() + order.getQuantity());
        medicineRepository.save(medicine);

        order.setStatus(OrderStatus.CANCELLED);
        order.setActive(false);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public MedicineOrderResponse getOrderById(String orderId) {
        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        return mapToResponse(order);
    }

    @Override
    public Page<MedicineOrderResponse> getUserOrders(String userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<MedicineOrder> orders = orderRepository.findAllByUserIdAndActiveTrue(userId, pageRequest);
        return orders.map(this::mapToResponse);
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusAndActiveTrue(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetweenAndActiveTrue(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByMedicine(String medicineId) {
        return orderRepository.findByMedicineIdAndActiveTrue(medicineId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateMedicineForOrder(Medicine medicine, Integer quantity) {
        if (!medicine.isActive()) {
            throw new ValidationException("Medicine is not available");
        }
        if (medicine.getStockQuantity() < quantity) {
            throw new ValidationException("Insufficient stock available");
        }
        if (medicine.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Medicine has expired");
        }
    }

    private MedicineOrderResponse mapToResponse(MedicineOrder order) {
        return MedicineOrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .medicineId(order.getMedicineId())
                .medicineName(order.getMedicineName())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .contactNumber(order.getContactNumber())
                .status(order.getStatus())
                .trackingNumber(order.getTrackingNumber())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
} 