package com.example.healthservice.service;

import com.example.healthservice.dto.MedicineRequest;
import com.example.healthservice.dto.MedicineResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MedicineService {
    MedicineResponse createMedicine(MedicineRequest request);
    MedicineResponse updateMedicine(String id, MedicineRequest request);
    void deleteMedicine(String id);
    Page<MedicineResponse> getAllMedicines(int page, int size);
    MedicineResponse getMedicineById(String id);
    List<MedicineResponse> getMedicinesByCategory(String category);
    List<MedicineResponse> getLowStockMedicines(Integer threshold);
    List<MedicineResponse> getExpiringMedicines();
} 