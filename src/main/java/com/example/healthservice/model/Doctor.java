package com.example.healthservice.model;

import com.example.healthservice.enums.Specialization;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private Specialization specialization;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
