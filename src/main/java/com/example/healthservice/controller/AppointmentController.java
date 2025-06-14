package com.example.healthservice.controller;

import com.example.healthservice.dto.request.AppointmentRequest;
import com.example.healthservice.dto.response.AppointmentResponse;
import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentRequest request) {
        logger.info("POST /appointments - Creating new appointment");
        AppointmentResponse response = appointmentService.createAppointment(request);
        logger.info("Successfully created appointment with ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        logger.info("GET /appointments/{} - Fetching appointment details", id);
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        logger.debug("Retrieved appointment details for ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /appointments - Fetching all appointments");
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());
        Page<AppointmentResponse> response = appointmentService.getAllAppointments(userId, entityId, pageable);
        logger.info("Retrieved {} appointments", response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable String id,
            @RequestBody AppointmentRequest request) {
        logger.info("PUT /appointments/{} - Updating appointment", id);
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        logger.info("Successfully updated appointment with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        logger.info("DELETE /appointments/{} - Deleting appointment", id);
        appointmentService.deleteAppointment(id);
        logger.info("Successfully deleted appointment with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AppointmentResponse>> getAppointmentsByStatus(
            @PathVariable AppointmentStatus status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status, userId, entityId, pageable));
    }

    @GetMapping("/deleted")
    public ResponseEntity<Page<AppointmentResponse>> getDeletedAppointments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());
        return ResponseEntity.ok(appointmentService.getDeletedAppointments(userId, entityId, pageable));
    }
}
