package com.example.healthservice.repository;

import com.example.healthservice.model.BloodBank;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodBankRepository extends MongoRepository<BloodBank, String> {
    List<BloodBank> findByLocationNear(Point location, Distance distance);
    List<BloodBank> findByActiveTrue();
} 