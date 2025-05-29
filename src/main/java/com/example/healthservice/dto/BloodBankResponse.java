package com.example.healthservice.dto;

import com.example.healthservice.enums.BloodType;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodBankResponse {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private double latitude;
    private double longitude;
    private Map<BloodType, Integer> bloodInventory;
    private String openingTime;
    private String closingTime;
} 