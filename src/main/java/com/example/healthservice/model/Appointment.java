package com.example.healthservice.model;

import com.example.healthservice.enums.AppointmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    private String id;
    private String userId;
    private String doctorId;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String googleEventId;
    private boolean deleted = false; // soft-delete flag
}
