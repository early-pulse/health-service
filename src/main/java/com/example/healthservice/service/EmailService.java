package com.example.healthservice.service;

import com.example.healthservice.model.Appointment;

public interface EmailService {
    void sendAppointmentConfirmation(Appointment appointment, String calendarLink);
} 