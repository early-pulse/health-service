package com.example.healthservice.service.impl;

import com.example.healthservice.dto.request.LabRequest;
import com.example.healthservice.dto.response.LabResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Lab;
import com.example.healthservice.repository.LabRepository;
import com.example.healthservice.service.GeoCodingService;
import com.example.healthservice.service.LabService;
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
public class LabServiceImpl implements LabService {
    private static final Logger logger = LoggerFactory.getLogger(LabServiceImpl.class);

    private final LabRepository labRepository;
    private final GeoCodingService geoCodingService;

    @Override
    public List<LabResponse> getAllLabs() {
        logger.debug("Fetching all labs");
        try {
            List<Lab> labs = labRepository.findAll();
            logger.info("Found {} labs", labs.size());
            return labs.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all labs", e);
            throw e;
        }
    }

    @Override
    public List<LabResponse> getLabsByLocation(double latitude, double longitude, double radiusKm) {
        logger.debug("Fetching labs near location: lat={}, lon={}, radius={}km", latitude, longitude, radiusKm);
        try {
            Point location = new Point(longitude, latitude);
            Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
            List<Lab> labs = labRepository.findByCoordinatesNear(location, distance);
            logger.info("Found {} labs within {}km of location", labs.size(), radiusKm);
            return labs.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching labs by location: lat={}, lon={}, radius={}km", 
                latitude, longitude, radiusKm, e);
            throw e;
        }
    }

    @Override
    public List<LabResponse> getLabsByTestName(String testName) {
        logger.debug("Fetching labs offering test: {}", testName);
        try {
            List<Lab> labs = labRepository.findByTestsOfferedContaining(testName);
            logger.info("Found {} labs offering test: {}", labs.size(), testName);
            return labs.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching labs by test name: {}", testName, e);
            throw e;
        }
    }

    @Override
    public LabResponse getLabById(String id) {
        logger.debug("Fetching lab with ID: {}", id);
        try {
            Lab lab = labRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", id);
                        return new ResourceNotFoundException("Lab not found with id " + id);
                    });
            logger.info("Found lab: {}", lab.getName());
            return mapToResponse(lab);
        } catch (Exception e) {
            logger.error("Error fetching lab with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public LabResponse createLab(LabRequest request) throws IOException, InterruptedException {
        logger.info("Creating new lab with ID: {}", request.getId());
        try {
            // Check if lab with ID already exists
            if (labRepository.existsById(request.getId())) {
                throw new IllegalArgumentException("Lab with ID " + request.getId() + " already exists");
            }

            GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
            Lab lab = Lab.builder()
                    .id(request.getId())
                    .name(request.getName())
                    .address(request.getAddress())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .testsOffered(request.getTestsOffered())
                    .active(true)
                    .coordinates(point)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Lab saved = labRepository.save(lab);
            logger.info("Lab created successfully with ID: {}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating lab with ID: {}", request.getId(), e);
            throw e;
        }
    }

    @Override
    public LabResponse updateLab(String id, LabRequest request) throws IOException, InterruptedException {
        logger.info("Updating lab with ID: {}", id);
        try {
            Lab existing = labRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", id);
                        return new ResourceNotFoundException("Lab not found with id " + id);
                    });
            
            existing.setName(request.getName());
            existing.setAddress(request.getAddress());
            existing.setEmail(request.getEmail());
            existing.setPhoneNumber(request.getPhoneNumber());
            existing.setTestsOffered(request.getTestsOffered());
            existing.setUpdatedAt(LocalDateTime.now());
            
            // Re-geocode if address changed
            if (!existing.getAddress().equals(request.getAddress())) {
                GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
                existing.setCoordinates(point);
            }
            
            Lab updated = labRepository.save(existing);
            logger.info("Lab updated successfully with ID: {}", id);
            return mapToResponse(updated);
        } catch (Exception e) {
            logger.error("Error updating lab with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void deleteLab(String id) {
        logger.info("Deleting lab with ID: {}", id);
        try {
            Lab existing = labRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", id);
                        return new ResourceNotFoundException("Lab not found with id " + id);
                    });
            existing.setActive(false);
            existing.setUpdatedAt(LocalDateTime.now());
            labRepository.save(existing);
            logger.info("Lab deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting lab with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<LabResponse> searchLabs(String name, String testName) {
        logger.debug("Searching labs with name: {} and testName: {}", name, testName);
        try {
            List<Lab> labs;
            if (name != null && testName != null) {
                labs = labRepository.findByNameContainingIgnoreCaseAndTestsOfferedContaining(name, testName);
            } else if (name != null) {
                labs = labRepository.findByNameContainingIgnoreCase(name);
            } else if (testName != null) {
                labs = labRepository.findByTestsOfferedContaining(testName);
            } else {
                labs = labRepository.findAll();
            }
            
            logger.info("Found {} labs matching search criteria", labs.size());
            return labs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching labs with name: {} and testName: {}", name, testName, e);
            throw e;
        }
    }

    private LabResponse mapToResponse(Lab lab) {
        return LabResponse.builder()
                .id(lab.getId())
                .name(lab.getName())
                .address(lab.getAddress())
                .email(lab.getEmail())
                .phoneNumber(lab.getPhoneNumber())
                .latitude(lab.getCoordinates().getY())
                .longitude(lab.getCoordinates().getX())
                .testsOffered(lab.getTestsOffered())
                .createdAt(lab.getCreatedAt())
                .updatedAt(lab.getUpdatedAt())
                .build();
    }
}
