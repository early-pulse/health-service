package com.example.healthservice.event;

import com.example.healthservice.model.Appointment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentCreatedEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentCreatedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }
} 