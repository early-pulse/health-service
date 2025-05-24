package com.example.healthservice.controller;

import com.example.healthservice.dto.AppointmentDTO;
import com.example.healthservice.bo.AppointmentRequestBO;
import com.example.healthservice.service.AppointmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Create new appointment
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
        @Valid @RequestBody AppointmentDTO appointmentDTO) {
        // Map DTO to BO for service call
        AppointmentRequestBO request = new AppointmentRequestBO(
            appointmentDTO.getUserId(),
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        );
        AppointmentDTO created = appointmentService.createAppointment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        AppointmentDTO dto = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get all appointments (with optional filters and pagination)
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointments(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String doctorId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<AppointmentDTO> result = appointmentService.getAllAppointments(
            userId, doctorId, PageRequest.of(page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Update appointment
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
        @PathVariable String id,
        @Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentRequestBO request = new AppointmentRequestBO(
            appointmentDTO.getUserId(),
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        );
        AppointmentDTO updated = appointmentService.updateAppointment(id, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Soft-delete appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
