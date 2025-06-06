package com.example.healthservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MedicineOrderRequest {
    private String medicineId;
    private Integer quantity;
    private String deliveryAddress;
    private String contactNumber;
} 