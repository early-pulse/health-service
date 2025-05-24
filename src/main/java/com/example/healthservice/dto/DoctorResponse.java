package com.example.healthservice.dto;

import com.example.healthservice.enums.Specialization;
import lombok.*;

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
}
