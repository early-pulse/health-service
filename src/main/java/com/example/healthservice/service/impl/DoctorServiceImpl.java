package com.example.healthservice.service.impl;

import com.example.healthservice.dto.request.DoctorRequest;
import com.example.healthservice.dto.response.DoctorResponse;
import com.example.healthservice.enums.Specialization;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Doctor;
import com.example.healthservice.repository.DoctorRepository;
import com.example.healthservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository repository;

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        logger.info("Creating new doctor with ID: {}", request.getId());
        
        try {
            // Check if doctor with ID already exists
            if (repository.existsById(request.getId())) {
                throw new IllegalArgumentException("Doctor with ID " + request.getId() + " already exists");
            }

            Doctor doctor = Doctor.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .specialization(request.getSpecialization())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            Doctor savedDoctor = repository.save(doctor);
            logger.info("Doctor created successfully with ID: {}", savedDoctor.getId());
            return mapToResponse(savedDoctor);
        } catch (Exception e) {
            logger.error("Error creating doctor with ID: {}", request.getId(), e);
            throw e;
        }
    }

    @Override
    public DoctorResponse updateDoctor(String id, DoctorRequest request) {
        logger.info("Updating doctor with ID: {}", id);
        
        try {
            Doctor doctor = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Doctor not found with ID: {}", id);
                    return new ResourceNotFoundException("Doctor not found");
                });
            
            doctor.setName(request.getName());
            doctor.setEmail(request.getEmail());
            doctor.setPhoneNumber(request.getPhoneNumber());
            doctor.setAddress(request.getAddress());
            doctor.setSpecialization(request.getSpecialization());
            doctor.setUpdatedAt(LocalDateTime.now());
            
            Doctor updatedDoctor = repository.save(doctor);
            logger.info("Doctor updated successfully with ID: {}", id);
            return mapToResponse(updatedDoctor);
        } catch (Exception e) {
            logger.error("Error updating doctor with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteDoctor(String id) {
        logger.info("Deleting doctor with ID: {}", id);
        
        try {
            Doctor doctor = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Doctor not found with ID: {}", id);
                    return new ResourceNotFoundException("Doctor not found");
                });
            
            doctor.setActive(false); // Soft delete
            doctor.setUpdatedAt(LocalDateTime.now());
            repository.save(doctor);
            logger.info("Doctor deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting doctor with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<DoctorResponse> getAllDoctors(int page, int size) {
        logger.debug("Fetching all doctors with page: {} and size: {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Doctor> doctorPage = repository.findAll(pageable);
            logger.info("Found {} doctors", doctorPage.getTotalElements());
            return doctorPage.map(this::mapToResponse);
        } catch (Exception e) {
            logger.error("Error fetching doctors with page: {} and size: {}", page, size, e);
            throw e;
        }
    }

    @Override
    public DoctorResponse getDoctorById(String id) {
        logger.debug("Fetching doctor with ID: {}", id);
        try {
            Doctor doctor = repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Doctor not found with ID: {}", id);
                    return new ResourceNotFoundException("Doctor not found with id " + id);
                });
            logger.info("Found doctor: {}", doctor.getName());
            return mapToResponse(doctor);
        } catch (Exception e) {
            logger.error("Error fetching doctor with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<DoctorResponse> getDoctorsBySpecialization(Specialization specialization) {
        logger.debug("Fetching doctors with specialization: {}", specialization);
        try {
            List<Doctor> doctors = repository.findBySpecialization(specialization);
            logger.info("Found {} doctors with specialization: {}", doctors.size(), specialization);
            return doctors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching doctors with specialization: {}", specialization, e);
            throw e;
        }
    }

    @Override
    public List<DoctorResponse> searchDoctors(String name, Specialization specialization) {
        logger.debug("Searching doctors with name: {} and specialization: {}", name, specialization);
        try {
            List<Doctor> doctors;
            if (name != null && specialization != null) {
                doctors = repository.findByNameContainingIgnoreCaseAndSpecialization(name, specialization);
            } else if (name != null) {
                doctors = repository.findByNameContainingIgnoreCase(name);
            } else if (specialization != null) {
                doctors = repository.findBySpecialization(specialization);
            } else {
                doctors = repository.findAll();
            }
            
            logger.info("Found {} doctors matching search criteria", doctors.size());
            return doctors.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching doctors with name: {} and specialization: {}", name, specialization, e);
            throw e;
        }
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
            .id(doctor.getId())
            .name(doctor.getName())
            .email(doctor.getEmail())
            .phoneNumber(doctor.getPhoneNumber())
            .address(doctor.getAddress())
            .specialization(doctor.getSpecialization())
            .latitude(doctor.getCoordinates() != null ? doctor.getCoordinates().getY() : 0.0)
            .longitude(doctor.getCoordinates() != null ? doctor.getCoordinates().getX() : 0.0)
            .createdAt(doctor.getCreatedAt())
            .updatedAt(doctor.getUpdatedAt())
            .build();
    }
}
