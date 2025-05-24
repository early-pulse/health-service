package com.example.healthservice.service;

import com.example.healthservice.dto.DoctorRequest;
import com.example.healthservice.dto.DoctorResponse;
import org.springframework.data.domain.Page;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse updateDoctor(String id, DoctorRequest request);
    void deleteDoctor(String id);
    Page<DoctorResponse> getAllDoctors(int page, int size);
}
