package com.example.healthservice.service.impl;

import com.example.healthservice.dto.request.MedicineRequest;
import com.example.healthservice.dto.response.MedicineResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Medicine;
import com.example.healthservice.repository.MedicineRepository;
import com.example.healthservice.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MedicineServiceImpl.class);

    private final MedicineRepository repository;

    @Override
    public MedicineResponse createMedicine(MedicineRequest request) {
        logger.info("Creating new medicine: {}", request.getName());
        try {
            Medicine medicine = Medicine.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .price(request.getPrice())
                    .stockQuantity(request.getStockQuantity())
                    .expiryDate(request.getExpiryDate())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Medicine savedMedicine = repository.save(medicine);
            logger.info("Medicine created successfully with ID: {}", savedMedicine.getId());
            return mapToResponse(savedMedicine);
        } catch (Exception e) {
            logger.error("Error creating medicine: {}", request.getName(), e);
            throw e;
        }
    }

    @Override
    public MedicineResponse updateMedicine(String id, MedicineRequest request) {
        logger.info("Updating medicine with ID: {}", id);
        try {
            Medicine medicine = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Medicine not found with ID: {}", id);
                        return new ResourceNotFoundException("Medicine not found with id " + id);
                    });

            medicine.setName(request.getName());
            medicine.setDescription(request.getDescription());
            medicine.setCategory(request.getCategory());
            medicine.setPrice(request.getPrice());
            medicine.setStockQuantity(request.getStockQuantity());
            medicine.setExpiryDate(request.getExpiryDate());
            medicine.setUpdatedAt(LocalDateTime.now());

            Medicine updatedMedicine = repository.save(medicine);
            logger.info("Medicine updated successfully with ID: {}", id);
            return mapToResponse(updatedMedicine);
        } catch (Exception e) {
            logger.error("Error updating medicine with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteMedicine(String id) {
        logger.info("Deleting medicine with ID: {}", id);
        try {
            Medicine medicine = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Medicine not found with ID: {}", id);
                        return new ResourceNotFoundException("Medicine not found with id " + id);
                    });
            medicine.setActive(false);
            medicine.setUpdatedAt(LocalDateTime.now());
            repository.save(medicine);
            logger.info("Medicine deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting medicine with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Page<MedicineResponse> getAllMedicines(int page, int size) {
        logger.debug("Fetching all medicines with page: {} and size: {}", page, size);
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
            Page<Medicine> medicinePage = repository.findAllByActiveTrue(pageRequest);
            logger.info("Found {} medicines", medicinePage.getTotalElements());
            return medicinePage.map(this::mapToResponse);
        } catch (Exception e) {
            logger.error("Error fetching medicines with page: {} and size: {}", page, size, e);
            throw e;
        }
    }

    @Override
    public MedicineResponse getMedicineById(String id) {
        logger.debug("Fetching medicine with ID: {}", id);
        try {
            Medicine medicine = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Medicine not found with ID: {}", id);
                        return new ResourceNotFoundException("Medicine not found with id " + id);
                    });
            logger.info("Found medicine: {}", medicine.getName());
            return mapToResponse(medicine);
        } catch (Exception e) {
            logger.error("Error fetching medicine with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<MedicineResponse> getMedicinesByCategory(String category) {
        logger.debug("Fetching medicines in category: {}", category);
        try {
            List<Medicine> medicines = repository.findByCategoryAndActiveTrue(category);
            logger.info("Found {} medicines in category: {}", medicines.size(), category);
            return medicines.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching medicines by category: {}", category, e);
            throw e;
        }
    }

    @Override
    public List<MedicineResponse> getLowStockMedicines(Integer threshold) {
        logger.debug("Fetching medicines with stock below threshold: {}", threshold);
        try {
            List<Medicine> medicines = repository.findByStockQuantityLessThanAndActiveTrue(threshold);
            logger.info("Found {} medicines with low stock", medicines.size());
            return medicines.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching low stock medicines with threshold: {}", threshold, e);
            throw e;
        }
    }

    @Override
    public List<MedicineResponse> getExpiringMedicines() {
        logger.debug("Fetching medicines expiring within 3 months");
        try {
            LocalDateTime threeMonthsFromNow = LocalDateTime.now().plusMonths(3);
            List<Medicine> medicines = repository.findByExpiryDateBeforeAndActiveTrue(threeMonthsFromNow);
            logger.info("Found {} medicines expiring within 3 months", medicines.size());
            return medicines.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching expiring medicines", e);
            throw e;
        }
    }

    private MedicineResponse mapToResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .description(medicine.getDescription())
                .category(medicine.getCategory())
                .price(medicine.getPrice())
                .stockQuantity(medicine.getStockQuantity())
                .expiryDate(medicine.getExpiryDate())
                .build();
    }
} 