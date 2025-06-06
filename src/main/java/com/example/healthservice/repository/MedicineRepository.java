package com.example.healthservice.repository;

import com.example.healthservice.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {
    Page<Medicine> findAllByActiveTrue(Pageable pageable);
    List<Medicine> findByCategoryAndActiveTrue(String category);
    List<Medicine> findByStockQuantityLessThanAndActiveTrue(Integer threshold);
    List<Medicine> findByExpiryDateBeforeAndActiveTrue(LocalDateTime date);
} 