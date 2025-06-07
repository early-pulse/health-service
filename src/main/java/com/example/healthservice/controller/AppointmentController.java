package com.example.healthservice.controller;

import com.example.healthservice.dto.AppointmentDTO;
import com.example.healthservice.bo.AppointmentRequestBO;
import com.example.healthservice.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    // Create new appointment
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
        @Valid @RequestBody AppointmentDTO appointmentDTO) {
        logger.info("POST /appointments - Creating new appointment");
        logger.debug("Appointment request details - userId: {}, doctorId: {}, dateTime: {}", 
            appointmentDTO.getUserId(), appointmentDTO.getDoctorId(), appointmentDTO.getAppointmentDateTime());

        // Map DTO to BO for service call
        AppointmentRequestBO request = new AppointmentRequestBO(
            appointmentDTO.getUserId(),
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        );
        AppointmentDTO created = appointmentService.createAppointment(request);
        logger.info("Successfully created appointment with ID: {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        logger.info("GET /appointments/{} - Fetching appointment details", id);
        
        AppointmentDTO dto = appointmentService.getAppointmentById(id);
        logger.debug("Retrieved appointment details for ID: {} - userId: {}, doctorId: {}", 
            id, dto.getUserId(), dto.getDoctorId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // Get all appointments (with optional filters and pagination)
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointments(
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String doctorId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /appointments - Fetching all appointments, page: {}, size: {}", page, size);
        logger.debug("Filter parameters - userId: {}, doctorId: {}", userId, doctorId);
        
        Page<AppointmentDTO> result = appointmentService.getAllAppointments(
            userId, doctorId, PageRequest.of(page, size));
        logger.info("Retrieved {} appointments", result.getTotalElements());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get appointments by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AppointmentDTO>> getAppointmentsByUserId(
        @PathVariable String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /appointments/user/{} - Fetching appointments for user, page: {}, size: {}", 
            userId, page, size);
        
        Page<AppointmentDTO> result = appointmentService.getAllAppointments(
            userId, null, PageRequest.of(page, size));
        logger.info("Retrieved {} appointments for user: {}", result.getTotalElements(), userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get appointments by doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<Page<AppointmentDTO>> getAppointmentsByDoctorId(
        @PathVariable String doctorId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /appointments/doctor/{} - Fetching appointments for doctor, page: {}, size: {}", 
            doctorId, page, size);
        
        Page<AppointmentDTO> result = appointmentService.getAllAppointments(
            null, doctorId, PageRequest.of(page, size));
        logger.info("Retrieved {} appointments for doctor: {}", result.getTotalElements(), doctorId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Update appointment
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
        @PathVariable String id,
        @Valid @RequestBody AppointmentDTO appointmentDTO) {
        logger.info("PUT /appointments/{} - Updating appointment", id);
        logger.debug("Update request details - userId: {}, doctorId: {}, dateTime: {}", 
            appointmentDTO.getUserId(), appointmentDTO.getDoctorId(), appointmentDTO.getAppointmentDateTime());

        AppointmentRequestBO request = new AppointmentRequestBO(
            appointmentDTO.getUserId(),
            appointmentDTO.getDoctorId(),
            appointmentDTO.getAppointmentDateTime()
        );
        AppointmentDTO updated = appointmentService.updateAppointment(id, request);
        logger.info("Successfully updated appointment with ID: {}", id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Soft-delete appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        logger.info("DELETE /appointments/{} - Deleting appointment", id);
        
        appointmentService.deleteAppointment(id);
        logger.info("Successfully deleted appointment with ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
