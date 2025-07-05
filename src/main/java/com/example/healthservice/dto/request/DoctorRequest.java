package com.example.healthservice.dto.request;

import com.example.healthservice.enums.Specialization;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorRequest {
    @NotBlank(message = "ID is required")
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Specialization is required")
    private Specialization specialization;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
