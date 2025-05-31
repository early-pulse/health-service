package com.example.healthservice.controller;

import com.example.healthservice.dto.LabRequest;
import com.example.healthservice.dto.LabResponse;
import com.example.healthservice.service.LabService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/labs")
@RequiredArgsConstructor
public class LabController {

    private final LabService labService;

    @GetMapping
    public List<LabResponse> getLabs(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String testName) {

        if (latitude != null && longitude != null && radius != null) {
            return labService.getLabsByLocation(latitude, longitude, radius);
        } else if (testName != null) {
            return labService.getLabsByTestName(testName);
        } else {
            return labService.getAllLabs();
        }
    }

    @GetMapping("/{labId}")
    public LabResponse getLabById(@PathVariable String labId) {
        return labService.getLabById(labId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('ADMIN')")
    public LabResponse createLab(@RequestBody LabRequest labRequest) throws IOException, InterruptedException {
        return labService.createLab(labRequest);
    }

    @PutMapping("/{labId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public LabResponse updateLab(@PathVariable String labId, @RequestBody LabRequest labRequest) throws IOException, InterruptedException {
        return labService.updateLab(labId, labRequest);
    }

    @DeleteMapping("/{labId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLab(@PathVariable String labId) {
        labService.deleteLab(labId);
    }
}
