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
            List<Lab> labs = labRepository.findByLocationNear(location, distance);
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
            List<Lab> labs = labRepository.findByTestNamesContaining(testName);
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
    public LabResponse getLabById(String labId) {
        logger.debug("Fetching lab with ID: {}", labId);
        try {
            Lab lab = labRepository.findById(labId)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", labId);
                        return new ResourceNotFoundException("Lab not found with id " + labId);
                    });
            logger.info("Found lab: {}", lab.getName());
            return mapToResponse(lab);
        } catch (Exception e) {
            logger.error("Error fetching lab with ID: {}", labId, e);
            throw e;
        }
    }

    @Override
    public LabResponse createLab(LabRequest labRequest) throws IOException, InterruptedException {
        logger.info("Creating new lab: {}", labRequest.getName());
        try {
            // Geocode address to GeoJsonPoint
            logger.debug("Geocoding address: {}", labRequest.getAddress());
            GeoJsonPoint point = geoCodingService.geocode(labRequest.getAddress());
            logger.debug("Geocoding successful: lat={}, lon={}", point.getY(), point.getX());

            Lab lab = Lab.builder()
                    .name(labRequest.getName())
                    .address(labRequest.getAddress())
                    .location(point)
                    .testNames(labRequest.getTestNames())
                    .build();

            Lab savedLab = labRepository.save(lab);
            logger.info("Lab created successfully with ID: {}", savedLab.getId());
            return mapToResponse(savedLab);
        } catch (Exception e) {
            logger.error("Error creating lab: {}", labRequest.getName(), e);
            throw e;
        }
    }

    @Override
    public LabResponse updateLab(String labId, LabRequest labRequest) throws IOException, InterruptedException {
        logger.info("Updating lab with ID: {}", labId);
        try {
            Lab lab = labRepository.findById(labId)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", labId);
                        return new ResourceNotFoundException("Lab not found with id " + labId);
                    });

            // Geocode address if it has changed
            if (!lab.getAddress().equals(labRequest.getAddress())) {
                logger.debug("Address changed, geocoding new address: {}", labRequest.getAddress());
                GeoJsonPoint point = geoCodingService.geocode(labRequest.getAddress());
                lab.setLocation(point);
                logger.debug("Geocoding successful: lat={}, lon={}", point.getY(), point.getX());
            }

            lab.setName(labRequest.getName());
            lab.setAddress(labRequest.getAddress());
            lab.setTestNames(labRequest.getTestNames());

            Lab updatedLab = labRepository.save(lab);
            logger.info("Lab updated successfully with ID: {}", labId);
            return mapToResponse(updatedLab);
        } catch (Exception e) {
            logger.error("Error updating lab with ID: {}", labId, e);
            throw e;
        }
    }

    @Override
    public void deleteLab(String labId) {
        logger.info("Deleting lab with ID: {}", labId);
        try {
            Lab lab = labRepository.findById(labId)
                    .orElseThrow(() -> {
                        logger.warn("Lab not found with ID: {}", labId);
                        return new ResourceNotFoundException("Lab not found with id " + labId);
                    });
            labRepository.delete(lab);
            logger.info("Lab deleted successfully with ID: {}", labId);
        } catch (Exception e) {
            logger.error("Error deleting lab with ID: {}", labId, e);
            throw e;
        }
    }

    private LabResponse mapToResponse(Lab lab) {
        return LabResponse.builder()
                .id(lab.getId())
                .name(lab.getName())
                .address(lab.getAddress())
                .latitude(lab.getLocation().getY())
                .longitude(lab.getLocation().getX())
                .testNames(lab.getTestNames())
                .build();
    }
}
