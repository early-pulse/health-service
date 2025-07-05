package com.example.healthservice.repository;

import com.example.healthservice.model.Lab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabRepository extends MongoRepository<Lab, String> {
    // Find labs within given distance of a point (uses MongoDB $near query)
    List<Lab> findByCoordinatesNear(Point location, Distance distance);

    // Find labs whose testsOffered list contains the given testName
    List<Lab> findByTestsOfferedContaining(String testName);

    List<Lab> findAll();

    Page<Lab> findAll(Pageable pageable);

    List<Lab> findByNameContainingIgnoreCaseAndTestsOfferedContaining(String name, String testName);

    List<Lab> findByNameContainingIgnoreCase(String name);
}
