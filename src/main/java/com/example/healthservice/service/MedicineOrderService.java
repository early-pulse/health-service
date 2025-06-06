package com.example.healthservice.service;

import com.example.healthservice.dto.MedicineOrderRequest;
import com.example.healthservice.dto.MedicineOrderResponse;
import com.example.healthservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicineOrderService {
    MedicineOrderResponse createOrder(String userId, MedicineOrderRequest request);
    MedicineOrderResponse updateOrderStatus(String orderId, OrderStatus status);
    void cancelOrder(String orderId);
    MedicineOrderResponse getOrderById(String orderId);
    Page<MedicineOrderResponse> getUserOrders(String userId, int page, int size);
    List<MedicineOrderResponse> getOrdersByStatus(OrderStatus status);
    List<MedicineOrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<MedicineOrderResponse> getOrdersByMedicine(String medicineId);
} 