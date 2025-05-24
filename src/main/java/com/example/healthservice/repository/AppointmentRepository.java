package com.example.healthservice.repository;

import com.example.healthservice.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    // Only non-deleted appointments
    Optional<Appointment> findByIdAndDeletedFalse(String id);

    Page<Appointment> findAllByDeletedFalse(Pageable pageable);
    Page<Appointment> findAllByUserIdAndDeletedFalse(String userId, Pageable pageable);
    Page<Appointment> findAllByDoctorIdAndDeletedFalse(String doctorId, Pageable pageable);
    Page<Appointment> findAllByUserIdAndDoctorIdAndDeletedFalse(String userId, String doctorId, Pageable pageable);
}