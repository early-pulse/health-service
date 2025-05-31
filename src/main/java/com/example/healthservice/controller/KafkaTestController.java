package com.example.healthservice.controller;

import com.example.healthservice.constant.AppConstants;
import com.example.healthservice.dto.AppointmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class KafkaTestController {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTestController.class);

    @Autowired
    private KafkaTemplate<String, AppointmentDTO> kafkaTemplate;

    @GetMapping("/kafka")
    public String testKafkaConnection() {
        try {
            // Create a test appointment
            AppointmentDTO testAppointment = new AppointmentDTO();
            testAppointment.setId(UUID.randomUUID().toString());
            testAppointment.setUserId("test-user");
            testAppointment.setDoctorId("test-doctor");
            testAppointment.setAppointmentDateTime(LocalDateTime.now());
            
            logger.info("Attempting to send message to appointments topic");
            // Send to appointments topic
            kafkaTemplate.send(AppConstants.APPOINTMENT_TOPIC, testAppointment);
            
            logger.info("Attempting to send message to notifications topic");
            // Send to notifications topic
            kafkaTemplate.send(AppConstants.NOTIFICATION_TOPIC, testAppointment);
            
            return "Successfully sent test messages to Kafka topics!";
        } catch (Exception e) {
            logger.error("Error testing Kafka connection", e);
            return "Error testing Kafka connection: " + e.getMessage() + "\nStack trace: " + e.getStackTrace()[0];
        }
    }
} 