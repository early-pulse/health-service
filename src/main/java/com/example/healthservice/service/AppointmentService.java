package com.example.healthservice.service;

import com.example.healthservice.dto.request.AppointmentRequest;
import com.example.healthservice.dto.response.AppointmentResponse;
import com.example.healthservice.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(String id);
    Page<AppointmentResponse> getAllAppointments(String userId, String entityId, Pageable pageable);
    AppointmentResponse updateAppointment(String id, AppointmentRequest request);
    void deleteAppointment(String id);
    Page<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, String userId, String entityId, Pageable pageable);
    Page<AppointmentResponse> getDeletedAppointments(String userId, String entityId, Pageable pageable);
}
