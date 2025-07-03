package com.example.healthservice.service.impl;

import com.example.healthservice.dto.request.AppointmentRequest;
import com.example.healthservice.dto.response.AppointmentResponse;
import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.enums.EntityType;
import com.example.healthservice.event.AppointmentCancelledEvent;
import com.example.healthservice.event.AppointmentCreatedEvent;
import com.example.healthservice.event.AppointmentUpdatedEvent;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Appointment;
import com.example.healthservice.repository.AppointmentRepository;
import com.example.healthservice.service.AppointmentService;
import com.example.healthservice.service.EmailService;
import com.example.healthservice.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final GoogleCalendarService googleCalendarService;
    private final EmailService emailService;

    @Override
    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.debug("Creating appointment for user: {} with entity: {}", request.getUserId(), request.getEntityId());
        try {
            // Validate test type for lab appointments
            if (request.getEntityType() == EntityType.LAB && (request.getTestType() == null || request.getTestType().trim().isEmpty())) {
                throw new IllegalArgumentException("Test type is required for lab appointments");
            }

            Appointment appointment = Appointment.builder()
                    .userId(request.getUserId())
                    .userEmail(request.getUserEmail())
                    .userName(request.getUserName())
                    .userPhone(request.getUserPhone())
                    .entityId(request.getEntityId())
                    .entityEmail(request.getEntityEmail())
                    .entityName(request.getEntityName())
                    .appointmentDateTime(request.getAppointmentDateTime())
                    .feedback(request.getFeedback())
                    .isLab(request.getEntityType() == EntityType.LAB)
                    .testType(request.getTestType())
                    .entityType(request.getEntityType())
                    .status(AppointmentStatus.SCHEDULED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deleted(false)
                    .reason(request.getReason())
                    .build();

            appointment = appointmentRepository.save(appointment);
            log.info("Appointment created successfully with ID: {}", appointment.getId());

            // Create Google Calendar event
            googleCalendarService.createEvent(appointment);

            // Publish appointment created event
            eventPublisher.publishEvent(new AppointmentCreatedEvent(this, appointment));

            return toResponse(appointment);
        } catch (Exception e) {
            log.error("Error creating appointment for user: {} with entity: {}", 
                    request.getUserId(), request.getEntityId(), e);
            throw e;
        }
    }

    @Override
    public AppointmentResponse getAppointmentById(String id) {
        log.debug("Fetching appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    log.warn("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id " + id);
                });
            log.debug("Appointment found: {}", appointment);
            return toResponse(appointment);
        } catch (Exception e) {
            log.error("Error fetching appointment with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<AppointmentResponse> getAllAppointments(String userId, String entityId, Pageable pageable) {
        log.debug("Fetching appointments with filters - userId: {}, entityId: {}, page: {}, size: {}", 
            userId, entityId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Appointment> page;
            if (userId != null && entityId != null) {
                log.debug("Fetching appointments for both user and entity");
                page = appointmentRepository.findAllByUserIdAndEntityIdAndDeletedFalse(userId, entityId, pageable);
            } else if (userId != null) {
                log.debug("Fetching appointments for user: {}", userId);
                page = appointmentRepository.findAllByUserIdAndDeletedFalse(userId, pageable);
            } else if (entityId != null) {
                log.debug("Fetching appointments for entity: {}", entityId);
                page = appointmentRepository.findAllByEntityIdAndDeletedFalse(entityId, pageable);
            } else {
                log.debug("Fetching all appointments");
                page = appointmentRepository.findAllByDeletedFalse(pageable);
            }
            
            log.info("Found {} appointments", page.getTotalElements());
            return page.map(this::toResponse);
        } catch (Exception e) {
            log.error("Error fetching appointments with filters - userId: {}, entityId: {}", 
                userId, entityId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointment(String id, AppointmentRequest request) {
        log.debug("Updating appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

            appointment.setAppointmentDateTime(request.getAppointmentDateTime());
            appointment.setFeedback(request.getFeedback());
            appointment.setStatus(request.getStatus());
            appointment.setUpdatedAt(LocalDateTime.now());
            appointment.setReason(request.getReason());
            appointment = appointmentRepository.save(appointment);

            // Update Google Calendar event
            googleCalendarService.updateEvent(appointment);

            // Publish appointment updated event
            eventPublisher.publishEvent(new AppointmentUpdatedEvent(this, appointment));

            return toResponse(appointment);
        } catch (Exception e) {
            log.error("Error updating appointment: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAppointment(String id) {
        log.debug("Deleting appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setUpdatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment);

            // Delete Google Calendar event
            String eventId = googleCalendarService.getEventId(appointment.getId());
            if (eventId != null) {
                googleCalendarService.deleteEvent(eventId);
            }

            // Publish appointment cancelled event
            eventPublisher.publishEvent(new AppointmentCancelledEvent(this, appointment));
        } catch (Exception e) {
            log.error("Error deleting appointment: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, String userId, String entityId, Pageable pageable) {
        log.debug("Fetching appointments with status: {}, userId: {}, entityId: {}", status, userId, entityId);
        try {
            Page<Appointment> page;
            if (userId != null && entityId != null) {
                page = appointmentRepository.findAllByStatusAndUserIdAndEntityIdOrderByAppointmentDateTimeDesc(status, userId, entityId, pageable);
            } else if (userId != null) {
                page = appointmentRepository.findAllByStatusAndUserIdOrderByAppointmentDateTimeDesc(status, userId, pageable);
            } else if (entityId != null) {
                page = appointmentRepository.findAllByStatusAndEntityIdOrderByAppointmentDateTimeDesc(status, entityId, pageable);
            } else {
                page = appointmentRepository.findAllByStatusAndDeletedFalseOrderByAppointmentDateTimeDesc(status, pageable);
            }
            log.info("Found {} appointments with status: {}", page.getTotalElements(), status);
            return page.map(this::toResponse);
        } catch (Exception e) {
            log.error("Error fetching appointments with status: {}", status, e);
            throw e;
        }
    }

    @Override
    public Page<AppointmentResponse> getDeletedAppointments(String userId, String entityId, Pageable pageable) {
        log.debug("Fetching deleted appointments with userId: {}, entityId: {}", userId, entityId);
        try {
            List<AppointmentStatus> historicalStatuses = Arrays.asList(
                AppointmentStatus.COMPLETED,
                AppointmentStatus.CANCELLED
            );

            Page<Appointment> page;
            if (userId != null && entityId != null) {
                page = appointmentRepository.findAllByStatusInAndDeletedTrueAndUserIdAndEntityIdOrderByAppointmentDateTimeDesc(
                    historicalStatuses, userId, entityId, pageable);
            } else if (userId != null) {
                page = appointmentRepository.findAllByStatusInAndDeletedTrueAndUserIdOrderByAppointmentDateTimeDesc(
                    historicalStatuses, userId, pageable);
            } else if (entityId != null) {
                page = appointmentRepository.findAllByStatusInAndDeletedTrueAndEntityIdOrderByAppointmentDateTimeDesc(
                    historicalStatuses, entityId, pageable);
            } else {
                page = appointmentRepository.findAllByStatusInAndDeletedTrueOrderByAppointmentDateTimeDesc(
                    historicalStatuses, pageable);
            }

            log.info("Found {} deleted appointments", page.getTotalElements());
            return page.map(this::toResponse);
        } catch (Exception e) {
            log.error("Error fetching deleted appointments", e);
            throw e;
        }
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .userId(appointment.getUserId())
                .userEmail(appointment.getUserEmail())
                .userName(appointment.getUserName())
                .userPhone(appointment.getUserPhone())
                .entityId(appointment.getEntityId())
                .entityEmail(appointment.getEntityEmail())
                .entityName(appointment.getEntityName())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .feedback(appointment.getFeedback())
                .isLab(appointment.getIsLab())
                .testType(appointment.getTestType())
                .entityType(appointment.getEntityType())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .deleted(appointment.isDeleted())
                .reason(appointment.getReason())
                .build();
    }
} 