package com.example.healthservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "medicines")
public class Medicine {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String manufacturer;
    private String category;
    private LocalDateTime expiryDate;
    private String batchNumber;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 