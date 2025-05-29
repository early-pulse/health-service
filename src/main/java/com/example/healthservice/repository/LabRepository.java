package com.example.healthservice.repository;

import com.example.healthservice.model.Lab;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabRepository extends MongoRepository<Lab, String> {
    // Find labs within given distance of a point (uses MongoDB $near query)
    List<Lab> findByLocationNear(Point location, Distance distance);

    // Find labs whose testNames list contains the given testName
    List<Lab> findByTestNamesContaining(String testName);
}
