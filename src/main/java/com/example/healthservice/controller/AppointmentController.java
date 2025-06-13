package com.example.healthservice.controller;

import com.example.healthservice.dto.request.AppointmentRequest;
import com.example.healthservice.dto.response.AppointmentResponse;
import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
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

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDateTime").descending());
        return ResponseEntity.ok(appointmentService.getAllAppointments(userId, entityId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable String id,
            @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok().build();
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
