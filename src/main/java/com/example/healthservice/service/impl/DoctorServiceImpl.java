package com.example.healthservice.service.impl;

import com.example.healthservice.dto.DoctorRequest;
import com.example.healthservice.dto.DoctorResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Doctor;
import com.example.healthservice.repository.DoctorRepository;
import com.example.healthservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository repository;

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        Doctor doctor = Doctor.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .specialization(request.getSpecialization())
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        repository.save(doctor);
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctor(String id, DoctorRequest request) {
        Doctor doctor = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setUpdatedAt(LocalDateTime.now());
        repository.save(doctor);
        return mapToResponse(doctor);
    }

    @Override
    public void deleteDoctor(String id) {
        Doctor doctor = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        doctor.setActive(false); // Soft delete
        doctor.setUpdatedAt(LocalDateTime.now());
        repository.save(doctor);
    }

    @Override
    public Page<DoctorResponse> getAllDoctors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Doctor> doctorPage = repository.findAllByActiveTrue(pageable);
        return doctorPage.map(this::mapToResponse);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
            .id(doctor.getId())
            .name(doctor.getName())
            .email(doctor.getEmail())
            .phone(doctor.getPhone())
            .specialization(doctor.getSpecialization())
            .build();
    }
}
