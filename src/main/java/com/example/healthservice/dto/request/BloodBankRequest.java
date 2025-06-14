package com.example.healthservice.dto.request;

import com.example.healthservice.enums.BloodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodBankRequest {
    @NotBlank(message = "ID is required")
    private String id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    private Map<BloodType, Integer> bloodInventory;
    
    @NotBlank(message = "Opening time is required")
    private String openingTime;
    
    @NotBlank(message = "Closing time is required")
    private String closingTime;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 