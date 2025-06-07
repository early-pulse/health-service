package com.example.healthservice.service.impl;

import com.example.healthservice.constant.AppConstants;
import com.example.healthservice.dto.AppointmentDTO;
import com.example.healthservice.bo.AppointmentRequestBO;
import com.example.healthservice.event.AppointmentEvent;
import com.example.healthservice.model.Appointment;
import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.repository.AppointmentRepository;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.service.AppointmentService;
import com.example.healthservice.utils.DateTimeUtil;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private com.google.api.services.calendar.Calendar googleCalendar;

    @Override
    @Transactional
    public AppointmentDTO createAppointment(AppointmentRequestBO request) {
        logger.info("Creating new appointment for user: {} with doctor: {} at time: {}", 
            request.getUserId(), request.getDoctorId(), request.getAppointmentDateTime());
        
        try {
            Appointment appointment = new Appointment();
            appointment.setUserId(request.getUserId());
            appointment.setDoctorId(request.getDoctorId());
            appointment.setAppointmentDateTime(request.getAppointmentDateTime());
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            appointment = appointmentRepository.save(appointment);
            logger.debug("Appointment saved to database with ID: {}", appointment.getId());

            Event event = new Event()
                .setSummary("Appointment for user " + appointment.getUserId())
                .setDescription("Doctor ID: " + appointment.getDoctorId());

            DateTime startDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime());
            DateTime endDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime().plusHours(1));

            event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC"));
            event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC"));

            try {
                logger.debug("Creating Google Calendar event for appointment: {}", appointment.getId());
                Event createdEvent = googleCalendar.events()
                    .insert(AppConstants.CALENDAR_ID, event)
                    .execute();
                appointment.setGoogleEventId(createdEvent.getId());
                appointmentRepository.save(appointment);
                logger.info("Google Calendar event created successfully with ID: {}", createdEvent.getId());
            } catch (Exception e) {
                logger.error("Failed to create Google Calendar event for appointment: {}", appointment.getId(), e);
                throw new RuntimeException("Failed to create Google Calendar event", e);
            }

            AppointmentDTO dto = toDTO(appointment);
            eventPublisher.publishEvent(new AppointmentEvent(this, dto, "CREATED"));
            logger.info("Appointment created successfully with ID: {}", appointment.getId());
            return dto;
        } catch (Exception e) {
            logger.error("Error creating appointment for user: {} with doctor: {}", 
                request.getUserId(), request.getDoctorId(), e);
            throw e;
        }
    }

    @Override
    public AppointmentDTO getAppointmentById(String id) {
        logger.debug("Fetching appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id " + id);
                });
            logger.debug("Appointment found: {}", appointment);
            return toDTO(appointment);
        } catch (Exception e) {
            logger.error("Error fetching appointment with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<AppointmentDTO> getAllAppointments(String userId, String doctorId, Pageable pageable) {
        logger.debug("Fetching appointments with filters - userId: {}, doctorId: {}, page: {}, size: {}", 
            userId, doctorId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Appointment> page;
            if (userId != null && doctorId != null) {
                logger.debug("Fetching appointments for both user and doctor");
                page = appointmentRepository.findAllByUserIdAndDoctorIdAndDeletedFalse(userId, doctorId, pageable);
            } else if (userId != null) {
                logger.debug("Fetching appointments for user: {}", userId);
                page = appointmentRepository.findAllByUserIdAndDeletedFalse(userId, pageable);
            } else if (doctorId != null) {
                logger.debug("Fetching appointments for doctor: {}", doctorId);
                page = appointmentRepository.findAllByDoctorIdAndDeletedFalse(doctorId, pageable);
            } else {
                logger.debug("Fetching all appointments");
                page = appointmentRepository.findAllByDeletedFalse(pageable);
            }
            
            logger.info("Found {} appointments", page.getTotalElements());
            return page.map(this::toDTO);
        } catch (Exception e) {
            logger.error("Error fetching appointments with filters - userId: {}, doctorId: {}", 
                userId, doctorId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AppointmentDTO updateAppointment(String id, AppointmentRequestBO request) {
        logger.info("Updating appointment: {} for user: {} with doctor: {}", 
            id, request.getUserId(), request.getDoctorId());
        
        try {
            Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id " + id);
                });

            appointment.setDoctorId(request.getDoctorId());
            appointment.setAppointmentDateTime(request.getAppointmentDateTime());
            appointment = appointmentRepository.save(appointment);
            logger.debug("Appointment updated in database: {}", appointment);

            if (appointment.getGoogleEventId() != null) {
                try {
                    logger.debug("Updating Google Calendar event for appointment: {}", id);
                    Event event = googleCalendar.events()
                        .get(AppConstants.CALENDAR_ID, appointment.getGoogleEventId())
                        .execute();

                    DateTime startDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime());
                    DateTime endDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime().plusHours(1));

                    event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC"));
                    event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC"));
                    event.setSummary("Updated appointment for user " + appointment.getUserId());

                    googleCalendar.events()
                        .update(AppConstants.CALENDAR_ID, event.getId(), event)
                        .execute();
                    logger.info("Google Calendar event updated successfully for appointment: {}", id);
                } catch (Exception e) {
                    logger.error("Failed to update Google Calendar event for appointment: {}", id, e);
                    throw new RuntimeException("Failed to update Google Calendar event", e);
                }
            }

            AppointmentDTO dto = toDTO(appointment);
            eventPublisher.publishEvent(new AppointmentEvent(this, dto, "UPDATED"));
            logger.info("Appointment updated successfully: {}", id);
            return dto;
        } catch (Exception e) {
            logger.error("Error updating appointment: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAppointment(String id) {
        logger.info("Deleting appointment: {}", id);
        try {
            Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with ID: {}", id);
                    return new ResourceNotFoundException("Appointment not found with id " + id);
                });
            
            appointment.setDeleted(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            logger.debug("Appointment marked as deleted in database: {}", id);

            if (appointment.getGoogleEventId() != null) {
                try {
                    logger.debug("Deleting Google Calendar event for appointment: {}", id);
                    googleCalendar.events()
                        .delete(AppConstants.CALENDAR_ID, appointment.getGoogleEventId())
                        .execute();
                    logger.info("Google Calendar event deleted successfully for appointment: {}", id);
                } catch (Exception e) {
                    logger.error("Failed to delete Google Calendar event for appointment: {}", id, e);
                    throw new RuntimeException("Failed to delete Google Calendar event", e);
                }
            }

            AppointmentDTO dto = toDTO(appointment);
            eventPublisher.publishEvent(new AppointmentEvent(this, dto, "CANCELLED"));
            logger.info("Appointment deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting appointment: {}", id, e);
            throw e;
        }
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
            .id(appointment.getId())
            .userId(appointment.getUserId())
            .doctorId(appointment.getDoctorId())
            .appointmentDateTime(appointment.getAppointmentDateTime())
            .status(appointment.getStatus())
            .build();
    }
}
