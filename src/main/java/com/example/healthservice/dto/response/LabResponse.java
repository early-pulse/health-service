package com.example.healthservice.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResponse {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private List<String> testNames;
}