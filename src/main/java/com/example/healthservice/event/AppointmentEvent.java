package com.example.healthservice.event;

import com.example.healthservice.dto.response.AppointmentResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentEvent extends ApplicationEvent {
    private final AppointmentResponse appointment;
    private final String eventType;

    public AppointmentEvent(Object source, AppointmentResponse appointment, String eventType) {
        super(source);
        this.appointment = appointment;
        this.eventType = eventType;
    }
} 