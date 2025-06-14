package com.example.healthservice.service;

import com.example.healthservice.dto.request.DoctorRequest;
import com.example.healthservice.dto.response.DoctorResponse;
import com.example.healthservice.enums.Specialization;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse updateDoctor(String id, DoctorRequest request);
    void deleteDoctor(String id);
    Page<DoctorResponse> getAllDoctors(int page, int size);
    DoctorResponse getDoctorById(String id);
    List<DoctorResponse> getDoctorsBySpecialization(Specialization specialization);
    List<DoctorResponse> searchDoctors(String name, Specialization specialization);
}
