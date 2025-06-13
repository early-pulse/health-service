package com.example.healthservice.dto.request;

import lombok.Data;

@Data
public class MedicineOrderRequest {
    private String medicineId;
    private Integer quantity;
    private String deliveryAddress;
    private String contactNumber;
} 