// AppointmentDTO.java
package com.example.healthservice.dto;

import com.example.healthservice.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String id;              // null for new appointments
    @NotNull
    private String userId;
    @NotNull
    private String doctorId;
    @NotNull
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
}
