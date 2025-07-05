package com.example.healthservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResponse {
    private String id;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private List<String> testsOffered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}