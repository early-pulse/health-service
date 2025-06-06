package com.example.healthservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MedicineResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String manufacturer;
    private String category;
    private LocalDateTime expiryDate;
    private String batchNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 