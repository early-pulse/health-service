package com.example.healthservice.dto.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabRequest {
    private String name;
    private String address;
    private List<String> testNames;
}