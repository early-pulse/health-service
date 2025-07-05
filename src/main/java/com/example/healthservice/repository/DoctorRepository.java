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
    Page<Doctor> findAll(Pageable pageable);
    List<Doctor> findBySpecialization(Specialization specialization);
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialization(String name, Specialization specialization);
    List<Doctor> findByNameContainingIgnoreCase(String name);
    List<Doctor> findAll();
}
