package com.example.healthservice.controller;

import com.example.healthservice.dto.DoctorRequest;
import com.example.healthservice.dto.DoctorResponse;
import com.example.healthservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody DoctorRequest request) {
        logger.info("POST /doctors - Creating new doctor");
        logger.debug("Doctor request details - name: {}, email: {}, specialization: {}", 
            request.getName(), request.getEmail(), request.getSpecialization());
        
        DoctorResponse response = doctorService.createDoctor(request);
        logger.info("Successfully created doctor with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable String id, @Valid @RequestBody DoctorRequest request) {
        logger.info("PUT /doctors/{} - Updating doctor", id);
        logger.debug("Update request details - name: {}, email: {}, specialization: {}", 
            request.getName(), request.getEmail(), request.getSpecialization());
        
        DoctorResponse response = doctorService.updateDoctor(id, request);
        logger.info("Successfully updated doctor with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("DELETE /doctors/{} - Deleting doctor", id);
        
        doctorService.deleteDoctor(id);
        logger.info("Successfully deleted doctor with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /doctors - Fetching all doctors, page: {}, size: {}", page, size);
        
        Page<DoctorResponse> response = doctorService.getAllDoctors(page, size);
        logger.info("Retrieved {} doctors", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
