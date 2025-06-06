package com.example.healthservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MedicineRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String manufacturer;
    private String category;
    private LocalDateTime expiryDate;
    private String batchNumber;
} 