package com.example.healthservice.service.impl;

import com.example.healthservice.dto.request.BloodBankRequest;
import com.example.healthservice.dto.response.BloodBankResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.BloodBank;
import com.example.healthservice.repository.BloodBankRepository;
import com.example.healthservice.service.BloodBankService;
import com.example.healthservice.service.GeoCodingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodBankServiceImpl implements BloodBankService {
    private static final Logger logger = LoggerFactory.getLogger(BloodBankServiceImpl.class);

    private final BloodBankRepository bloodBankRepository;
    private final GeoCodingService geoCodingService;

    @Override
    public List<BloodBankResponse> getAllBloodBanks() {
        logger.debug("Fetching all blood banks");
        try {
            List<BloodBank> bloodBanks = bloodBankRepository.findByActiveTrue();
            logger.info("Found {} blood banks", bloodBanks.size());
            return bloodBanks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all blood banks", e);
            throw e;
        }
    }

    @Override
    public List<BloodBankResponse> getBloodBanksByLocation(double latitude, double longitude, double radiusKm) {
        logger.debug("Searching blood banks near coordinates: ({}, {}) within radius: {} km", latitude, longitude, radiusKm);
        try {
            Point location = new Point(longitude, latitude);
            Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
            List<BloodBank> bloodBanks = bloodBankRepository.findByLocationNear(location, distance);
            logger.info("Found {} blood banks within {} km of coordinates ({}, {})", 
                    bloodBanks.size(), radiusKm, latitude, longitude);
            return bloodBanks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching blood banks by location: ({}, {}) radius: {}", 
                    latitude, longitude, radiusKm, e);
            throw e;
        }
    }

    @Override
    public BloodBankResponse getBloodBankById(String id) {
        logger.debug("Fetching blood bank with ID: {}", id);
        try {
            BloodBank bloodBank = bloodBankRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Blood bank not found with ID: {}", id);
                        return new ResourceNotFoundException("Blood bank not found with id " + id);
                    });
            logger.info("Found blood bank: {}", bloodBank.getName());
            return mapToResponse(bloodBank);
        } catch (Exception e) {
            logger.error("Error fetching blood bank with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public BloodBankResponse createBloodBank(BloodBankRequest request) throws IOException, InterruptedException {
        logger.info("Creating new blood bank with ID: {}", request.getId());
        try {
            // Check if blood bank with ID already exists
            if (bloodBankRepository.existsById(request.getId())) {
                throw new IllegalArgumentException("Blood bank with ID " + request.getId() + " already exists");
            }

            GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
            BloodBank bloodBank = BloodBank.builder()
                    .id(request.getId())
                    .name(request.getName())
                    .address(request.getAddress())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .bloodInventory(request.getBloodInventory())
                    .openingTime(request.getOpeningTime())
                    .closingTime(request.getClosingTime())
                    .active(true)
                    .location(point)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            BloodBank saved = bloodBankRepository.save(bloodBank);
            logger.info("Blood bank created successfully with ID: {}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating blood bank with ID: {}", request.getId(), e);
            throw e;
        }
    }

    @Override
    public BloodBankResponse updateBloodBank(String id, BloodBankRequest request) throws IOException, InterruptedException {
        logger.info("Updating blood bank with ID: {}", id);
        try {
            BloodBank existing = bloodBankRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Blood bank not found with ID: {}", id);
                        return new ResourceNotFoundException("Blood bank not found with id " + id);
                    });
            
            existing.setName(request.getName());
            existing.setAddress(request.getAddress());
            existing.setPhone(request.getPhone());
            existing.setEmail(request.getEmail());
            existing.setBloodInventory(request.getBloodInventory());
            existing.setOpeningTime(request.getOpeningTime());
            existing.setClosingTime(request.getClosingTime());
            existing.setUpdatedAt(LocalDateTime.now());
            
            // Re-geocode if address changed
            if (!existing.getAddress().equals(request.getAddress())) {
                GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
                existing.setLocation(point);
            }
            
            BloodBank updated = bloodBankRepository.save(existing);
            logger.info("Blood bank updated successfully with ID: {}", id);
            return mapToResponse(updated);
        } catch (Exception e) {
            logger.error("Error updating blood bank with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteBloodBank(String id) {
        logger.info("Deleting blood bank with ID: {}", id);
        try {
            BloodBank existing = bloodBankRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Blood bank not found with ID: {}", id);
                        return new ResourceNotFoundException("Blood bank not found with id " + id);
                    });
            existing.setActive(false);
            existing.setUpdatedAt(LocalDateTime.now());
            bloodBankRepository.save(existing);
            logger.info("Blood bank deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting blood bank with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<BloodBankResponse> searchBloodBanks(String name, String bloodType) {
        logger.debug("Searching blood banks with name: {} and bloodType: {}", name, bloodType);
        try {
            List<BloodBank> bloodBanks;
            if (name != null && bloodType != null) {
                bloodBanks = bloodBankRepository.findByNameAndBloodTypeAndActiveTrue(name, bloodType);
            } else if (name != null) {
                bloodBanks = bloodBankRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
            } else if (bloodType != null) {
                bloodBanks = bloodBankRepository.findByBloodTypeAndActiveTrue(bloodType);
            } else {
                bloodBanks = bloodBankRepository.findByActiveTrue();
            }
            
            logger.info("Found {} blood banks matching search criteria", bloodBanks.size());
            return bloodBanks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching blood banks with name: {} and bloodType: {}", name, bloodType, e);
            throw e;
        }
    }

    private BloodBankResponse mapToResponse(BloodBank bloodBank) {
        return BloodBankResponse.builder()
                .id(bloodBank.getId())
                .name(bloodBank.getName())
                .address(bloodBank.getAddress())
                .phone(bloodBank.getPhone())
                .email(bloodBank.getEmail())
                .latitude(bloodBank.getLocation().getY())
                .longitude(bloodBank.getLocation().getX())
                .bloodInventory(bloodBank.getBloodInventory())
                .openingTime(bloodBank.getOpeningTime())
                .closingTime(bloodBank.getClosingTime())
                .build();
    }
}