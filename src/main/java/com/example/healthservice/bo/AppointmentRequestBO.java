// AppointmentRequestBO.java
package com.example.healthservice.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Business Object for appointment creation/update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestBO {
    @NotNull
    private String userId;
    @NotNull
    private String doctorId;
    @NotNull
    private LocalDateTime appointmentDateTime;
}
