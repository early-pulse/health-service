package com.example.healthservice.kafka.producer;

import com.example.healthservice.constant.AppConstants;
import com.example.healthservice.dto.AppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AppointmentEventProducer {

    @Autowired
    private KafkaTemplate<String, AppointmentDTO> kafkaTemplate;

    /**
     * Send a message about a newly created appointment.
     */
    public void sendAppointmentCreatedEvent(AppointmentDTO appointmentDTO) {
        kafkaTemplate.send(AppConstants.APPOINTMENT_TOPIC, appointmentDTO);
    }

    /**
     * Send a notification event for the appointment (e.g., to trigger email/SMS).
     */
    public void sendNotificationEvent(AppointmentDTO appointmentDTO) {
        kafkaTemplate.send(AppConstants.NOTIFICATION_TOPIC, appointmentDTO);
    }

    public void sendAppointmentEvent(AppointmentDTO appointment) {
        kafkaTemplate.send(AppConstants.APPOINTMENT_TOPIC, appointment);
    }
}
