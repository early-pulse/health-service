package com.example.healthservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.healthservice.enums.OrderStatus;

@Data
@Builder
@Document(collection = "medicine_orders")
public class MedicineOrder {
    @Id
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
    private boolean active;
} 