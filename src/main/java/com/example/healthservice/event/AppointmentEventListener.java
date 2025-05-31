package com.example.healthservice.event;

import com.example.healthservice.dto.AppointmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentEventListener.class);

    @Async
    @EventListener
    public void handleAppointmentEvent(AppointmentEvent event) {
        AppointmentDTO appointment = event.getAppointment();
        String eventType = event.getEventType();
        
        logger.info("Processing appointment event: {} for appointment: {}", eventType, appointment.getId());
        
        // Here you can add your notification logic
        // For example, sending emails, SMS, or other notifications
        switch (eventType) {
            case "CREATED":
                sendAppointmentCreatedNotification(appointment);
                break;
            case "UPDATED":
                sendAppointmentUpdatedNotification(appointment);
                break;
            case "CANCELLED":
                sendAppointmentCancelledNotification(appointment);
                break;
            default:
                logger.warn("Unknown event type: {}", eventType);
        }
    }

    private void sendAppointmentCreatedNotification(AppointmentDTO appointment) {
        // Implement your notification logic here
        logger.info("Sending appointment created notification for appointment: {}", appointment.getId());
    }

    private void sendAppointmentUpdatedNotification(AppointmentDTO appointment) {
        // Implement your notification logic here
        logger.info("Sending appointment updated notification for appointment: {}", appointment.getId());
    }

    private void sendAppointmentCancelledNotification(AppointmentDTO appointment) {
        // Implement your notification logic here
        logger.info("Sending appointment cancelled notification for appointment: {}", appointment.getId());
    }
} 