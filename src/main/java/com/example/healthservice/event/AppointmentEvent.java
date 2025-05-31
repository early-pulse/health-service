package com.example.healthservice.event;

import com.example.healthservice.dto.AppointmentDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentEvent extends ApplicationEvent {
    private final AppointmentDTO appointment;
    private final String eventType;

    public AppointmentEvent(Object source, AppointmentDTO appointment, String eventType) {
        super(source);
        this.appointment = appointment;
        this.eventType = eventType;
    }
} 