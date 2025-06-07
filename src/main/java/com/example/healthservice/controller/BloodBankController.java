package com.example.healthservice.controller;

import com.example.healthservice.dto.BloodBankRequest;
import com.example.healthservice.dto.BloodBankResponse;
import com.example.healthservice.service.BloodBankService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/blood-banks")
@RequiredArgsConstructor
public class BloodBankController {
    private static final Logger logger = LoggerFactory.getLogger(BloodBankController.class);

    private final BloodBankService bloodBankService;

    @GetMapping
    public ResponseEntity<List<BloodBankResponse>> getBloodBanks(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius) {

        if (latitude != null && longitude != null && radius != null) {
            logger.info("GET /blood-banks - Fetching blood banks by location - lat: {}, lng: {}, radius: {}", 
                latitude, longitude, radius);
            List<BloodBankResponse> response = bloodBankService.getBloodBanksByLocation(latitude, longitude, radius);
            logger.info("Retrieved {} blood banks in the specified location", response.size());
            return ResponseEntity.ok(response);
        } else {
            logger.info("GET /blood-banks - Fetching all blood banks");
            List<BloodBankResponse> response = bloodBankService.getAllBloodBanks();
            logger.info("Retrieved {} blood banks", response.size());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodBankResponse> getBloodBankById(@PathVariable String id) {
        logger.info("GET /blood-banks/{} - Fetching blood bank details", id);
        
        BloodBankResponse response = bloodBankService.getBloodBankById(id);
        logger.debug("Retrieved blood bank details for ID: {} - name: {}", id, response.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BloodBankResponse> createBloodBank(@RequestBody BloodBankRequest request) throws IOException, InterruptedException {
        logger.info("POST /blood-banks - Creating new blood bank");
        logger.debug("Blood bank request details - name: {}, address: {}, phone: {}", 
            request.getName(), request.getAddress(), request.getPhone());
        
        BloodBankResponse response = bloodBankService.createBloodBank(request);
        logger.info("Successfully created blood bank with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloodBankResponse> updateBloodBank(
            @PathVariable String id, 
            @RequestBody BloodBankRequest request) throws IOException, InterruptedException {
        logger.info("PUT /blood-banks/{} - Updating blood bank", id);
        logger.debug("Update request details - name: {}, address: {}, phone: {}", 
            request.getName(), request.getAddress(), request.getPhone());
        
        BloodBankResponse response = bloodBankService.updateBloodBank(id, request);
        logger.info("Successfully updated blood bank with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBloodBank(@PathVariable String id) {
        logger.info("DELETE /blood-banks/{} - Deleting blood bank", id);
        
        bloodBankService.deleteBloodBank(id);
        logger.info("Successfully deleted blood bank with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
} 