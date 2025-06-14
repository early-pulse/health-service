package com.example.healthservice.repository;

import com.example.healthservice.model.BloodBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodBankRepository extends MongoRepository<BloodBank, String> {
    List<BloodBank> findByLocationNear(Point location, Distance distance);
    List<BloodBank> findByActiveTrue();
    Page<BloodBank> findAllByActiveTrue(Pageable pageable);
    List<BloodBank> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    
    @Query("{ 'active': true, 'bloodInventory.?0': { $exists: true } }")
    List<BloodBank> findByBloodTypeAndActiveTrue(String bloodType);
    
    @Query("{ 'active': true, 'name': { $regex: ?0, $options: 'i' }, 'bloodInventory.?1': { $exists: true } }")
    List<BloodBank> findByNameAndBloodTypeAndActiveTrue(String name, String bloodType);
} 