package com.example.healthservice.repository;

import com.example.healthservice.enums.OrderStatus;
import com.example.healthservice.model.MedicineOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicineOrderRepository extends MongoRepository<MedicineOrder, String> {
    Page<MedicineOrder> findAllByUserIdAndActiveTrue(String userId, Pageable pageable);
    List<MedicineOrder> findByStatusAndActiveTrue(OrderStatus status);
    List<MedicineOrder> findByOrderDateBetweenAndActiveTrue(LocalDateTime startDate, LocalDateTime endDate);
    List<MedicineOrder> findByMedicineIdAndActiveTrue(String medicineId);
} 