package com.example.healthservice.repository;

import com.example.healthservice.enums.Specialization;
import com.example.healthservice.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Page<Doctor> findAllByActiveTrue(Pageable pageable);
    List<Doctor> findBySpecializationAndActiveTrue(Specialization specialization);
    List<Doctor> findByNameContainingIgnoreCaseAndSpecializationAndActiveTrue(String name, Specialization specialization);
    List<Doctor> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    List<Doctor> findByActiveTrue();
}
