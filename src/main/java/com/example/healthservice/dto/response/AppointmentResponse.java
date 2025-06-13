package com.example.healthservice.dto.response;

import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.enums.EntityType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private String id;
    private String userId;
    private String userEmail;
    private String userName;
    private String userPhone;
    private String entityId;
    private String entityEmail;
    private String entityName;
    private LocalDateTime appointmentDateTime;
    private boolean isLab;
    private String testType;
    private EntityType entityType;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
} 