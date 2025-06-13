package com.example.healthservice.dto.request;

import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.enums.EntityType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid user email format")
    private String userEmail;
    
    @NotBlank(message = "User name is required")
    private String userName;
    
    @NotBlank(message = "User phone is required")
    private String userPhone;
    
    @NotBlank(message = "Entity ID is required")
    private String entityId;
    
    @NotBlank(message = "Entity email is required")
    @Email(message = "Invalid entity email format")
    private String entityEmail;
    
    @NotBlank(message = "Entity name is required")
    private String entityName;

    @NotNull(message = "Is lab flag is required")
    private Boolean isLab;
    
    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;
    
    private AppointmentStatus status;

    @NotNull(message = "Entity type is required")
    private EntityType entityType;

    private String testType;  // Required for lab appointments
} 