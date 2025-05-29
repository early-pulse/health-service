package com.example.healthservice.service.impl;

import com.example.healthservice.dto.LabRequest;
import com.example.healthservice.dto.LabResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.Lab;
import com.example.healthservice.repository.LabRepository;
import com.example.healthservice.service.GeoCodingService;
import com.example.healthservice.service.LabService;
import lombok.RequiredArgsConstructor;
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

    private final LabRepository labRepository;
    private final GeoCodingService geoCodingService;

    @Override
    public List<LabResponse> getAllLabs() {
        return labRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LabResponse> getLabsByLocation(double latitude, double longitude, double radiusKm) {
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
        List<Lab> labs = labRepository.findByLocationNear(location, distance);
        return labs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<LabResponse> getLabsByTestName(String testName) {
        return labRepository.findByTestNamesContaining(testName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LabResponse getLabById(String labId) {
        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab not found with id " + labId));
        return mapToResponse(lab);
    }

    @Override
    public LabResponse createLab(LabRequest labRequest) throws IOException, InterruptedException {
        // Geocode address to GeoJsonPoint
        GeoJsonPoint point = geoCodingService.geocode(labRequest.getAddress());
        Lab lab = Lab.builder()
                .name(labRequest.getName())
                .address(labRequest.getAddress())
                .location(point)
                .testNames(labRequest.getTestNames())
                .build();
        Lab saved = labRepository.save(lab);
        return mapToResponse(saved);
    }

    @Override
    public LabResponse updateLab(String labId, LabRequest labRequest) throws IOException, InterruptedException {
        Lab existing = labRepository.findById(labId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab not found with id " + labId));
        existing.setName(labRequest.getName());
        existing.setAddress(labRequest.getAddress());
        // Re-geocode if address changed
        GeoJsonPoint point = geoCodingService.geocode(labRequest.getAddress());
        existing.setLocation(point);
        existing.setTestNames(labRequest.getTestNames());
        Lab updated = labRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteLab(String labId) {
        Lab existing = labRepository.findById(labId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab not found with id " + labId));
        labRepository.delete(existing);
    }

    // Helper to convert entity to DTO
    private LabResponse mapToResponse(Lab lab) {
        return LabResponse.builder()
                .id(lab.getId())
                .name(lab.getName())
                .address(lab.getAddress())
                .latitude(lab.getLocation().getY())   // getY() returns latitude
                .longitude(lab.getLocation().getX())  // getX() returns longitude
                .testNames(lab.getTestNames())
                .build();
    }
}
