package com.example.healthservice.service.impl;

import com.example.healthservice.dto.BloodBankRequest;
import com.example.healthservice.dto.BloodBankResponse;
import com.example.healthservice.exception.ResourceNotFoundException;
import com.example.healthservice.model.BloodBank;
import com.example.healthservice.repository.BloodBankRepository;
import com.example.healthservice.service.BloodBankService;
import com.example.healthservice.service.GeoCodingService;
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
public class BloodBankServiceImpl implements BloodBankService {

    private final BloodBankRepository bloodBankRepository;
    private final GeoCodingService geoCodingService;

    @Override
    public List<BloodBankResponse> getAllBloodBanks() {
        return bloodBankRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloodBankResponse> getBloodBanksByLocation(double latitude, double longitude, double radiusKm) {
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
        List<BloodBank> bloodBanks = bloodBankRepository.findByLocationNear(location, distance);
        return bloodBanks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public BloodBankResponse getBloodBankById(String id) {
        BloodBank bloodBank = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood bank not found with id " + id));
        return mapToResponse(bloodBank);
    }

    @Override
    public BloodBankResponse createBloodBank(BloodBankRequest request) throws IOException, InterruptedException {
        GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
        BloodBank bloodBank = BloodBank.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .location(point)
                .bloodInventory(request.getBloodInventory())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .active(true)
                .build();
        BloodBank saved = bloodBankRepository.save(bloodBank);
        return mapToResponse(saved);
    }

    @Override
    public BloodBankResponse updateBloodBank(String id, BloodBankRequest request) throws IOException, InterruptedException {
        BloodBank existing = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood bank not found with id " + id));
        
        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setBloodInventory(request.getBloodInventory());
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        
        // Re-geocode if address changed
        GeoJsonPoint point = geoCodingService.geocode(request.getAddress());
        existing.setLocation(point);
        
        BloodBank updated = bloodBankRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteBloodBank(String id) {
        BloodBank existing = bloodBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood bank not found with id " + id));
        existing.setActive(false);
        bloodBankRepository.save(existing);
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