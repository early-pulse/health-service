package com.example.healthservice.controller;

import com.example.healthservice.dto.MedicineOrderRequest;
import com.example.healthservice.dto.MedicineOrderResponse;
import com.example.healthservice.enums.OrderStatus;
import com.example.healthservice.service.MedicineOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class MedicineOrderController {
    private static final Logger logger = LoggerFactory.getLogger(MedicineOrderController.class);

    private final MedicineOrderService orderService;

    @PostMapping
    public ResponseEntity<MedicineOrderResponse> createOrder(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody MedicineOrderRequest request) {
        logger.info("POST /orders - Creating new order for user: {}", userId);
        logger.debug("Order request details - medicineId: {}, quantity: {}, deliveryAddress: {}", 
            request.getMedicineId(), request.getQuantity(), request.getDeliveryAddress());
        
        MedicineOrderResponse response = orderService.createOrder(userId, request);
        logger.info("Successfully created order with ID: {} for user: {}", response.getId(), userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<MedicineOrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        logger.info("PUT /orders/{}/status - Updating order status to: {}", orderId, status);
        
        MedicineOrderResponse response = orderService.updateOrderStatus(orderId, status);
        logger.info("Successfully updated order status to: {} for order: {}", status, orderId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        logger.info("DELETE /orders/{} - Cancelling order", orderId);
        
        orderService.cancelOrder(orderId);
        logger.info("Successfully cancelled order: {}", orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<MedicineOrderResponse> getOrderById(@PathVariable String orderId) {
        logger.info("GET /orders/{} - Fetching order details", orderId);
        
        MedicineOrderResponse response = orderService.getOrderById(orderId);
        logger.debug("Retrieved order details for order: {} with status: {}", orderId, response.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<MedicineOrderResponse>> getUserOrders(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /orders/user - Fetching orders for user: {}, page: {}, size: {}", userId, page, size);
        
        List<MedicineOrderResponse> response = orderService.getUserOrders(userId, page, size).getContent();
        logger.info("Retrieved {} orders for user: {}", response.size(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.info("GET /orders/status/{} - Fetching orders by status", status);
        
        List<MedicineOrderResponse> response = orderService.getOrdersByStatus(status);
        logger.info("Retrieved {} orders with status: {}", response.size(), status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("GET /orders/date-range - Fetching orders between {} and {}", startDate, endDate);
        
        List<MedicineOrderResponse> response = orderService.getOrdersByDateRange(startDate, endDate);
        logger.info("Retrieved {} orders in date range", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByMedicine(@PathVariable String medicineId) {
        logger.info("GET /orders/medicine/{} - Fetching orders for medicine", medicineId);
        
        List<MedicineOrderResponse> response = orderService.getOrdersByMedicine(medicineId);
        logger.info("Retrieved {} orders for medicine: {}", response.size(), medicineId);
        return ResponseEntity.ok(response);
    }
} 