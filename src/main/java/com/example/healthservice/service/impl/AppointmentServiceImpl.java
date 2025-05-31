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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private com.google.api.services.calendar.Calendar googleCalendar;

    @Override
    @Transactional
    public AppointmentDTO createAppointment(AppointmentRequestBO request) {
        Appointment appointment = new Appointment();
        appointment.setUserId(request.getUserId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment = appointmentRepository.save(appointment);

        Event event = new Event()
            .setSummary("Appointment for user " + appointment.getUserId())
            .setDescription("Doctor ID: " + appointment.getDoctorId());

        DateTime startDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime());
        DateTime endDateTime = DateTimeUtil.toDateTime(appointment.getAppointmentDateTime().plusHours(1));

        event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC"));

        try {
            Event createdEvent = googleCalendar.events()
                .insert(AppConstants.CALENDAR_ID, event)
                .execute();
            appointment.setGoogleEventId(createdEvent.getId());
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Calendar event", e);
        }

        AppointmentDTO dto = toDTO(appointment);
        eventPublisher.publishEvent(new AppointmentEvent(this, dto, "CREATED"));
        return dto;
    }

    @Override
    public AppointmentDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
        return toDTO(appointment);
    }

    @Override
    public Page<AppointmentDTO> getAllAppointments(String userId, String doctorId, Pageable pageable) {
        Page<Appointment> page;
        if (userId != null && doctorId != null) {
            page = appointmentRepository.findAllByUserIdAndDoctorIdAndDeletedFalse(userId, doctorId, pageable);
        } else if (userId != null) {
            page = appointmentRepository.findAllByUserIdAndDeletedFalse(userId, pageable);
        } else if (doctorId != null) {
            page = appointmentRepository.findAllByDoctorIdAndDeletedFalse(doctorId, pageable);
        } else {
            page = appointmentRepository.findAllByDeletedFalse(pageable);
        }
        return page.map(this::toDTO);
    }

    @Override
    @Transactional
    public AppointmentDTO updateAppointment(String id, AppointmentRequestBO request) {
        Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));

        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment = appointmentRepository.save(appointment);

        if (appointment.getGoogleEventId() != null) {
            try {
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
            } catch (Exception e) {
                throw new RuntimeException("Failed to update Google Calendar event", e);
            }
        }

        AppointmentDTO dto = toDTO(appointment);
        eventPublisher.publishEvent(new AppointmentEvent(this, dto, "UPDATED"));
        return dto;
    }

    @Override
    @Transactional
    public void deleteAppointment(String id) {
        Appointment appointment = appointmentRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
        appointment.setDeleted(true);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        if (appointment.getGoogleEventId() != null) {
            try {
                googleCalendar.events()
                    .delete(AppConstants.CALENDAR_ID, appointment.getGoogleEventId())
                    .execute();
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete Google Calendar event", e);
            }
        }

        AppointmentDTO dto = toDTO(appointment);
        eventPublisher.publishEvent(new AppointmentEvent(this, dto, "CANCELLED"));
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setUserId(appointment.getUserId());
        dto.setDoctorId(appointment.getDoctorId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setStatus(appointment.getStatus());
        return dto;
    }
}
