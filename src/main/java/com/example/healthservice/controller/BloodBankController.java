package com.example.healthservice.controller;

import com.example.healthservice.dto.BloodBankRequest;
import com.example.healthservice.dto.BloodBankResponse;
import com.example.healthservice.service.BloodBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blood-banks")
@RequiredArgsConstructor
public class BloodBankController {

    private final BloodBankService bloodBankService;

    @GetMapping
    public List<BloodBankResponse> getBloodBanks(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius) {

        if (latitude != null && longitude != null && radius != null) {
            return bloodBankService.getBloodBanksByLocation(latitude, longitude, radius);
        } else {
            return bloodBankService.getAllBloodBanks();
        }
    }

    @GetMapping("/{id}")
    public BloodBankResponse getBloodBankById(@PathVariable String id) {
        return bloodBankService.getBloodBankById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BloodBankResponse createBloodBank(@RequestBody BloodBankRequest request) throws IOException, InterruptedException {
        return bloodBankService.createBloodBank(request);
    }

    @PutMapping("/{id}")
    public BloodBankResponse updateBloodBank(@PathVariable String id, @RequestBody BloodBankRequest request) throws IOException, InterruptedException {
        return bloodBankService.updateBloodBank(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBloodBank(@PathVariable String id) {
        bloodBankService.deleteBloodBank(id);
    }
} 