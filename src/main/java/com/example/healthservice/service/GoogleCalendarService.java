package com.example.healthservice.service;

import com.example.healthservice.model.Appointment;

public interface GoogleCalendarService {
    void createEvent(Appointment appointment);
    void updateEvent(Appointment appointment);
    void deleteEvent(String eventId);
    String getEventId(String appointmentId);
    String getEventLink(String appointmentId);
} 