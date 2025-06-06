package com.example.healthservice.controller;

import com.example.healthservice.dto.MedicineOrderRequest;
import com.example.healthservice.dto.MedicineOrderResponse;
import com.example.healthservice.enums.OrderStatus;
import com.example.healthservice.service.MedicineOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class MedicineOrderController {

    private final MedicineOrderService orderService;

    @PostMapping
    public ResponseEntity<MedicineOrderResponse> createOrder(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody MedicineOrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(userId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<MedicineOrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<MedicineOrderResponse> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<MedicineOrderResponse>> getUserOrders(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, page, size).getContent());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate));
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<MedicineOrderResponse>> getOrdersByMedicine(@PathVariable String medicineId) {
        return ResponseEntity.ok(orderService.getOrdersByMedicine(medicineId));
    }
} 