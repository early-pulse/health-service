package com.example.healthservice.dto;

import com.example.healthservice.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MedicineOrderResponse {
    private String id;
    private String userId;
    private String medicineId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private String contactNumber;
    private OrderStatus status;
    private String trackingNumber;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 