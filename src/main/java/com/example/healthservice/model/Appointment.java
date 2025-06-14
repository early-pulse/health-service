package com.example.healthservice.model;

import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private String userId;
    private String userEmail;
    private String userName;
    private String userPhone;
    private String entityId; // Can be doctorId or labId
    private String entityEmail;
    private String entityName; // Name of doctor or lab
    private Boolean isLab; // true for lab, false for doctor
    private String testType;  // For lab appointments
    private LocalDateTime appointmentDateTime;
    private EntityType entityType;
    private AppointmentStatus status;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String googleEventId;
    private boolean deleted = false; // soft-delete flag
}
