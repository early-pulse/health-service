package com.example.healthservice.repository;

import com.example.healthservice.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
  Page<Doctor> findAllByActiveTrue(Pageable pageable);
}
