package com.example.healthservice.repository;

import com.example.healthservice.enums.AppointmentStatus;
import com.example.healthservice.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    @Query("{ 'id': ?0, 'deleted': false }")
    java.util.Optional<Appointment> findByIdAndDeletedFalse(String id);

    @Query("{ 'userId': ?0, 'deleted': false }")
    Page<Appointment> findAllByUserIdAndDeletedFalse(String userId, Pageable pageable);

    @Query("{ 'entityId': ?0, 'deleted': false }")
    Page<Appointment> findAllByEntityIdAndDeletedFalse(String entityId, Pageable pageable);

    @Query("{ 'userId': ?0, 'entityId': ?1, 'deleted': false }")
    Page<Appointment> findAllByUserIdAndEntityIdAndDeletedFalse(String userId, String entityId, Pageable pageable);

    Page<Appointment> findAllByStatusAndUserIdAndEntityIdOrderByAppointmentDateTimeDesc(AppointmentStatus status, String userId, String entityId, Pageable pageable);

    Page<Appointment> findAllByStatusAndUserIdOrderByAppointmentDateTimeDesc(AppointmentStatus status, String userId, Pageable pageable);

    Page<Appointment> findAllByStatusAndEntityIdOrderByAppointmentDateTimeDesc(AppointmentStatus status, String entityId, Pageable pageable);

    @Query("{ 'status': ?0, 'deleted': false }")
    Page<Appointment> findAllByStatusAndDeletedFalseOrderByAppointmentDateTimeDesc(AppointmentStatus status, Pageable pageable);

    @Query("{ 'status': { $in: ?0 }, 'deleted': true }")
    Page<Appointment> findAllByStatusInAndDeletedTrueOrderByAppointmentDateTimeDesc(List<AppointmentStatus> statuses, Pageable pageable);

    @Query("{ 'status': { $in: ?0 }, 'deleted': true, 'userId': ?1 }")
    Page<Appointment> findAllByStatusInAndDeletedTrueAndUserIdOrderByAppointmentDateTimeDesc(List<AppointmentStatus> statuses, String userId, Pageable pageable);

    @Query("{ 'status': { $in: ?0 }, 'deleted': true, 'entityId': ?1 }")
    Page<Appointment> findAllByStatusInAndDeletedTrueAndEntityIdOrderByAppointmentDateTimeDesc(List<AppointmentStatus> statuses, String entityId, Pageable pageable);

    @Query("{ 'status': { $in: ?0 }, 'deleted': true, 'userId': ?1, 'entityId': ?2 }")
    Page<Appointment> findAllByStatusInAndDeletedTrueAndUserIdAndEntityIdOrderByAppointmentDateTimeDesc(List<AppointmentStatus> statuses, String userId, String entityId, Pageable pageable);

    @Query("{ 'deleted': false }")
    Page<Appointment> findAllByDeletedFalse(Pageable pageable);
}