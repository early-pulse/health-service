package com.example.healthservice.service;

import com.example.healthservice.dto.BloodBankRequest;
import com.example.healthservice.dto.BloodBankResponse;

import java.io.IOException;
import java.util.List;

public interface BloodBankService {
    List<BloodBankResponse> getAllBloodBanks();
    List<BloodBankResponse> getBloodBanksByLocation(double latitude, double longitude, double radiusKm);
    BloodBankResponse getBloodBankById(String id);
    BloodBankResponse createBloodBank(BloodBankRequest request) throws IOException, InterruptedException;
    BloodBankResponse updateBloodBank(String id, BloodBankRequest request) throws IOException, InterruptedException;
    void deleteBloodBank(String id);
} 