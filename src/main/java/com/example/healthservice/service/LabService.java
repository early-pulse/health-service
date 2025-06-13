package com.example.healthservice.service;

import com.example.healthservice.dto.request.LabRequest;
import com.example.healthservice.dto.response.LabResponse;

import java.io.IOException;
import java.util.List;

public interface LabService {
    List<LabResponse> getAllLabs();
    List<LabResponse> getLabsByLocation(double latitude, double longitude, double radiusKm);
    List<LabResponse> getLabsByTestName(String testName);
    LabResponse getLabById(String labId);
    LabResponse createLab(LabRequest labRequest) throws IOException, InterruptedException;
    LabResponse updateLab(String labId, LabRequest labRequest) throws IOException, InterruptedException;
    void deleteLab(String labId);
}