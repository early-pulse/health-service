package com.example.healthservice.service;

import com.example.healthservice.bo.AppointmentRequestBO;
import com.example.healthservice.dto.AppointmentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentDTO createAppointment(AppointmentRequestBO appointmentRequest);
    AppointmentDTO getAppointmentById(String id);
    Page<AppointmentDTO> getAllAppointments(String userId, String doctorId, Pageable pageable);
    AppointmentDTO updateAppointment(String id, AppointmentRequestBO appointmentRequest);
    void deleteAppointment(String id);
}
