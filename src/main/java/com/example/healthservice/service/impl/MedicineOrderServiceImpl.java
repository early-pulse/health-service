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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MedicineOrderServiceImpl.class);

    private final MedicineOrderRepository orderRepository;
    private final MedicineRepository medicineRepository;

    @Override
    @Transactional
    public MedicineOrderResponse createOrder(String userId, MedicineOrderRequest request) {
        logger.info("Creating new medicine order for user: {}", userId);
        logger.debug("Order request details - medicineId: {}, quantity: {}", request.getMedicineId(), request.getQuantity());

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> {
                    logger.error("Medicine not found with id: {}", request.getMedicineId());
                    return new ResourceNotFoundException("Medicine not found with id " + request.getMedicineId());
                });

        logger.debug("Found medicine: {} (ID: {})", medicine.getName(), medicine.getId());

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

        logger.debug("Created order object with total price: {}", order.getTotalPrice());

        // Update medicine stock
        medicine.setStockQuantity(medicine.getStockQuantity() - request.getQuantity());
        medicineRepository.save(medicine);
        logger.info("Updated medicine stock. New quantity: {}", medicine.getStockQuantity());

        MedicineOrder savedOrder = orderRepository.save(order);
        logger.info("Successfully created order with ID: {}", savedOrder.getId());
        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public MedicineOrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        logger.info("Updating order status - orderId: {}, new status: {}", orderId, status);

        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new ResourceNotFoundException("Order not found with id " + orderId);
                });

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            logger.warn("Cannot update status of {} order. Current status: {}", orderId, order.getStatus());
            throw new ValidationException("Cannot update status of cancelled or refunded order");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
            logger.debug("Set delivery date for order: {}", orderId);
        }

        MedicineOrder updatedOrder = orderRepository.save(order);
        logger.info("Successfully updated order status to: {}", status);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        logger.info("Cancelling order: {}", orderId);

        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new ResourceNotFoundException("Order not found with id " + orderId);
                });

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED) {
            logger.warn("Cannot cancel order that is already {} - orderId: {}", order.getStatus(), orderId);
            throw new ValidationException("Cannot cancel order that is already shipped or delivered");
        }

        // Restore medicine stock
        Medicine medicine = medicineRepository.findById(order.getMedicineId())
                .orElseThrow(() -> {
                    logger.error("Medicine not found with id: {}", order.getMedicineId());
                    return new ResourceNotFoundException("Medicine not found with id " + order.getMedicineId());
                });

        medicine.setStockQuantity(medicine.getStockQuantity() + order.getQuantity());
        medicineRepository.save(medicine);
        logger.info("Restored medicine stock. New quantity: {}", medicine.getStockQuantity());

        order.setStatus(OrderStatus.CANCELLED);
        order.setActive(false);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        logger.info("Successfully cancelled order: {}", orderId);
    }

    @Override
    public MedicineOrderResponse getOrderById(String orderId) {
        logger.debug("Fetching order by ID: {}", orderId);

        MedicineOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new ResourceNotFoundException("Order not found with id " + orderId);
                });

        logger.debug("Found order: {} with status: {}", orderId, order.getStatus());
        return mapToResponse(order);
    }

    @Override
    public Page<MedicineOrderResponse> getUserOrders(String userId, int page, int size) {
        logger.debug("Fetching orders for user: {}, page: {}, size: {}", userId, page, size);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<MedicineOrder> orders = orderRepository.findAllByUserIdAndActiveTrue(userId, pageRequest);
        
        logger.info("Found {} orders for user: {}", orders.getTotalElements(), userId);
        return orders.map(this::mapToResponse);
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByStatus(OrderStatus status) {
        logger.debug("Fetching orders with status: {}", status);

        List<MedicineOrder> orders = orderRepository.findByStatusAndActiveTrue(status);
        logger.info("Found {} orders with status: {}", orders.size(), status);
        
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Fetching orders between {} and {}", startDate, endDate);

        List<MedicineOrder> orders = orderRepository.findByOrderDateBetweenAndActiveTrue(startDate, endDate);
        logger.info("Found {} orders in date range", orders.size());
        
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineOrderResponse> getOrdersByMedicine(String medicineId) {
        logger.debug("Fetching orders for medicine: {}", medicineId);

        List<MedicineOrder> orders = orderRepository.findByMedicineIdAndActiveTrue(medicineId);
        logger.info("Found {} orders for medicine: {}", orders.size(), medicineId);
        
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateMedicineForOrder(Medicine medicine, Integer quantity) {
        logger.debug("Validating medicine for order - medicine: {}, quantity: {}", medicine.getId(), quantity);

        if (!medicine.isActive()) {
            logger.warn("Medicine {} is not active", medicine.getId());
            throw new ValidationException("Medicine is not available");
        }
        if (medicine.getStockQuantity() < quantity) {
            logger.warn("Insufficient stock for medicine {}. Available: {}, Requested: {}", 
                medicine.getId(), medicine.getStockQuantity(), quantity);
            throw new ValidationException("Insufficient stock available");
        }
        if (medicine.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("Medicine {} has expired. Expiry date: {}", medicine.getId(), medicine.getExpiryDate());
            throw new ValidationException("Medicine has expired");
        }

        logger.debug("Medicine validation successful for: {}", medicine.getId());
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