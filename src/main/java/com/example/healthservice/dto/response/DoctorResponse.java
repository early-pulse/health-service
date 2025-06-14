package com.example.healthservice.dto.response;

import com.example.healthservice.enums.Specialization;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Specialization specialization;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
