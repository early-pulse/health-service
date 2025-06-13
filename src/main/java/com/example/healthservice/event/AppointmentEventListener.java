package com.example.healthservice.event;

import com.example.healthservice.model.Appointment;
import com.example.healthservice.service.EmailService;
import com.example.healthservice.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventListener {

    private final EmailService emailService;
    private final GoogleCalendarService googleCalendarService;

    @Async
    @EventListener
    public void handleAppointmentCreatedEvent(AppointmentCreatedEvent event) {
        try {
            Appointment appointment = event.getAppointment();
            String calendarLink = googleCalendarService.getEventLink(appointment.getId());
            emailService.sendAppointmentConfirmation(appointment, calendarLink);
            log.info("Appointment confirmation emails sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Error handling appointment created event", e);
        }
    }

    @Async
    @EventListener
    public void handleAppointmentUpdatedEvent(AppointmentUpdatedEvent event) {
        try {
            Appointment appointment = event.getAppointment();
            String calendarLink = googleCalendarService.getEventLink(appointment.getId());
            emailService.sendAppointmentConfirmation(appointment, calendarLink);
            log.info("Appointment update emails sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Error handling appointment updated event", e);
        }
    }

    @Async
    @EventListener
    public void handleAppointmentCancelledEvent(AppointmentCancelledEvent event) {
        try {
            Appointment appointment = event.getAppointment();
            emailService.sendAppointmentConfirmation(appointment, null);
            log.info("Appointment cancellation emails sent for appointment: {}", appointment.getId());
        } catch (Exception e) {
            log.error("Error handling appointment cancelled event", e);
        }
    }
} 