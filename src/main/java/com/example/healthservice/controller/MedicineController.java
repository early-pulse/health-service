package com.example.healthservice.controller;

import com.example.healthservice.dto.request.MedicineRequest;
import com.example.healthservice.dto.response.MedicineResponse;
import com.example.healthservice.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private static final Logger logger = LoggerFactory.getLogger(MedicineController.class);

    private final MedicineService medicineService;

    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(@RequestBody MedicineRequest request) {
        logger.info("POST /medicines - Creating new medicine");
        logger.debug("Medicine request details - name: {}, category: {}, price: {}", 
            request.getName(), request.getCategory(), request.getPrice());
        
        MedicineResponse response = medicineService.createMedicine(request);
        logger.info("Successfully created medicine with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("GET /medicines - Fetching all medicines, page: {}, size: {}", page, size);
        
        List<MedicineResponse> response = medicineService.getAllMedicines(page, size).getContent();
        logger.info("Retrieved {} medicines", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable String id) {
        logger.info("GET /medicines/{} - Fetching medicine details", id);
        
        MedicineResponse response = medicineService.getMedicineById(id);
        logger.debug("Retrieved medicine details for ID: {} - name: {}", id, response.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable String id,
            @RequestBody MedicineRequest request) {
        logger.info("PUT /medicines/{} - Updating medicine", id);
        logger.debug("Update request details - name: {}, category: {}, price: {}", 
            request.getName(), request.getCategory(), request.getPrice());
        
        MedicineResponse response = medicineService.updateMedicine(id, request);
        logger.info("Successfully updated medicine with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String id) {
        logger.info("DELETE /medicines/{} - Deleting medicine", id);
        
        medicineService.deleteMedicine(id);
        logger.info("Successfully deleted medicine with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MedicineResponse>> getMedicinesByCategory(@PathVariable String category) {
        logger.info("GET /medicines/category/{} - Fetching medicines by category", category);
        
        List<MedicineResponse> response = medicineService.getMedicinesByCategory(category);
        logger.info("Retrieved {} medicines in category: {}", response.size(), category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<MedicineResponse>> getLowStockMedicines(
            @RequestParam(defaultValue = "10") Integer threshold) {
        logger.info("GET /medicines/low-stock - Fetching low stock medicines, threshold: {}", threshold);
        
        List<MedicineResponse> response = medicineService.getLowStockMedicines(threshold);
        logger.info("Retrieved {} medicines with stock below threshold", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<MedicineResponse>> getExpiringMedicines() {
        logger.info("GET /medicines/expiring - Fetching expiring medicines");
        
        List<MedicineResponse> response = medicineService.getExpiringMedicines();
        logger.info("Retrieved {} expiring medicines", response.size());
        return ResponseEntity.ok(response);
    }
} 