package com.example.healthservice.controller;

import com.example.healthservice.dto.LabRequest;
import com.example.healthservice.dto.LabResponse;
import com.example.healthservice.service.LabService;
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
    public ResponseEntity<List<LabResponse>> getLabs(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String testName) {
        
        if (latitude != null && longitude != null && radius != null) {
            logger.info("GET /labs - Fetching labs by location - lat: {}, lng: {}, radius: {}", 
                latitude, longitude, radius);
            List<LabResponse> response = labService.getLabsByLocation(latitude, longitude, radius);
            logger.info("Retrieved {} labs in the specified location", response.size());
            return ResponseEntity.ok(response);
        } else if (testName != null) {
            logger.info("GET /labs - Fetching labs by test name: {}", testName);
            List<LabResponse> response = labService.getLabsByTestName(testName);
            logger.info("Retrieved {} labs offering test: {}", response.size(), testName);
            return ResponseEntity.ok(response);
        } else {
            logger.info("GET /labs - Fetching all labs");
            List<LabResponse> response = labService.getAllLabs();
            logger.info("Retrieved {} labs", response.size());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{labId}")
    public ResponseEntity<LabResponse> getLabById(@PathVariable String labId) {
        logger.info("GET /labs/{} - Fetching lab details", labId);
        
        LabResponse response = labService.getLabById(labId);
        logger.debug("Retrieved lab details for ID: {} - name: {}", labId, response.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabResponse> createLab(@RequestBody LabRequest labRequest) throws IOException, InterruptedException {
        logger.info("POST /labs - Creating new lab");
        logger.debug("Lab request details - name: {}, address: {}, testNames: {}", 
            labRequest.getName(), labRequest.getAddress(), labRequest.getTestNames());
        
        LabResponse response = labService.createLab(labRequest);
        logger.info("Successfully created lab with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{labId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabResponse> updateLab(
            @PathVariable String labId, 
            @RequestBody LabRequest labRequest) throws IOException, InterruptedException {
        logger.info("PUT /labs/{} - Updating lab", labId);
        logger.debug("Update request details - name: {}, address: {}, testNames: {}", 
            labRequest.getName(), labRequest.getAddress(), labRequest.getTestNames());
        
        LabResponse response = labService.updateLab(labId, labRequest);
        logger.info("Successfully updated lab with ID: {}", labId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{labId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLab(@PathVariable String labId) {
        logger.info("DELETE /labs/{} - Deleting lab", labId);
        
        labService.deleteLab(labId);
        logger.info("Successfully deleted lab with ID: {}", labId);
        return ResponseEntity.noContent().build();
    }
}
