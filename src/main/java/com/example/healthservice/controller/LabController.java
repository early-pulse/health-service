package com.example.healthservice.controller;

import com.example.healthservice.dto.request.LabRequest;
import com.example.healthservice.dto.response.LabResponse;
import com.example.healthservice.service.LabService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/labs")
@RequiredArgsConstructor
public class LabController {
    private static final Logger logger = LoggerFactory.getLogger(LabController.class);

    private final LabService labService;

    @GetMapping
    public ResponseEntity<List<LabResponse>> getAllLabs() {
        logger.info("GET /labs - Fetching all labs");
        List<LabResponse> response = labService.getAllLabs();
        logger.info("Retrieved {} labs", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResponse> getLabById(@PathVariable String id) {
        logger.info("GET /labs/{} - Fetching lab details", id);
        
        LabResponse response = labService.getLabById(id);
        logger.debug("Retrieved lab details for ID: {} - name: {}", id, response.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabResponse> createLab(@Valid @RequestBody LabRequest request) throws IOException, InterruptedException {
        logger.info("POST /labs - Creating new lab");
        logger.debug("Lab request details - name: {}, address: {}, testNames: {}", 
            request.getName(), request.getAddress(), request.getTestNames());
        
        LabResponse response = labService.createLab(request);
        logger.info("Successfully created lab with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabResponse> updateLab(@PathVariable String id, @Valid @RequestBody LabRequest request) throws IOException, InterruptedException {
        logger.info("PUT /labs/{} - Updating lab", id);
        logger.debug("Update request details - name: {}, address: {}, testNames: {}", 
            request.getName(), request.getAddress(), request.getTestNames());
        
        LabResponse response = labService.updateLab(id, request);
        logger.info("Successfully updated lab with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLab(@PathVariable String id) {
        logger.info("DELETE /labs/{} - Deleting lab", id);
        
        labService.deleteLab(id);
        logger.info("Successfully deleted lab with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<LabResponse>> getLabsByLocation(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm) {
        logger.info("GET /labs/nearby - Fetching labs by location - lat: {}, lng: {}, radius: {}", 
            latitude, longitude, radiusKm);
        List<LabResponse> response = labService.getLabsByLocation(latitude, longitude, radiusKm);
        logger.info("Retrieved {} labs in the specified location", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-test/{testName}")
    public ResponseEntity<List<LabResponse>> getLabsByTestName(@PathVariable String testName) {
        logger.info("GET /labs/by-test/{} - Fetching labs by test name: {}", testName);
        List<LabResponse> response = labService.getLabsByTestName(testName);
        logger.info("Retrieved {} labs offering test: {}", response.size(), testName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LabResponse>> searchLabs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String testName) {
        logger.info("GET /labs/search - Searching labs");
        List<LabResponse> response = labService.searchLabs(name, testName);
        logger.info("Retrieved {} labs matching the search criteria", response.size());
        return ResponseEntity.ok(response);
    }
}
