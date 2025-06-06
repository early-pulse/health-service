package com.example.healthservice.service.impl;

import com.example.healthservice.dto.MedicineRequest;
import com.example.healthservice.dto.MedicineResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Medicine;
import com.example.healthservice.repository.MedicineRepository;
import com.example.healthservice.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository repository;

    @Override
    public MedicineResponse createMedicine(MedicineRequest request) {
        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .manufacturer(request.getManufacturer())
                .category(request.getCategory())
                .expiryDate(request.getExpiryDate())
                .batchNumber(request.getBatchNumber())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Medicine saved = repository.save(medicine);
        return mapToResponse(saved);
    }

    @Override
    public MedicineResponse updateMedicine(String id, MedicineRequest request) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + id));
        
        medicine.setName(request.getName());
        medicine.setDescription(request.getDescription());
        medicine.setPrice(request.getPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setCategory(request.getCategory());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setBatchNumber(request.getBatchNumber());
        medicine.setUpdatedAt(LocalDateTime.now());
        
        Medicine updated = repository.save(medicine);
        return mapToResponse(updated);
    }

    @Override
    public void deleteMedicine(String id) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + id));
        medicine.setActive(false);
        medicine.setUpdatedAt(LocalDateTime.now());
        repository.save(medicine);
    }

    @Override
    public Page<MedicineResponse> getAllMedicines(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Medicine> medicinePage = repository.findAllByActiveTrue(pageRequest);
        return medicinePage.map(this::mapToResponse);
    }

    @Override
    public MedicineResponse getMedicineById(String id) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id " + id));
        return mapToResponse(medicine);
    }

    @Override
    public List<MedicineResponse> getMedicinesByCategory(String category) {
        return repository.findByCategoryAndActiveTrue(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineResponse> getLowStockMedicines(Integer threshold) {
        return repository.findByStockQuantityLessThanAndActiveTrue(threshold).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineResponse> getExpiringMedicines() {
        LocalDateTime threeMonthsFromNow = LocalDateTime.now().plusMonths(3);
        return repository.findByExpiryDateBeforeAndActiveTrue(threeMonthsFromNow).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MedicineResponse mapToResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .description(medicine.getDescription())
                .price(medicine.getPrice())
                .stockQuantity(medicine.getStockQuantity())
                .manufacturer(medicine.getManufacturer())
                .category(medicine.getCategory())
                .expiryDate(medicine.getExpiryDate())
                .batchNumber(medicine.getBatchNumber())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
} 