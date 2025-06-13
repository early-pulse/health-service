package com.example.healthservice.event;

import com.example.healthservice.model.Appointment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentUpdatedEvent extends ApplicationEvent {
    private final Appointment appointment;

    public AppointmentUpdatedEvent(Object source, Appointment appointment) {
        super(source);
        this.appointment = appointment;
    }
} 