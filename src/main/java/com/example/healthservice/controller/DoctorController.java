package com.example.healthservice.controller;

import com.example.healthservice.dto.DoctorRequest;
import com.example.healthservice.dto.DoctorResponse;
import com.example.healthservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public DoctorResponse create(@Valid @RequestBody DoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @PutMapping("/{id}")
    public DoctorResponse update(@PathVariable String id, @Valid @RequestBody DoctorRequest request) {
        return doctorService.updateDoctor(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        doctorService.deleteDoctor(id);
    }

    @GetMapping
    public Page<DoctorResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return doctorService.getAllDoctors(page, size);
    }
}
