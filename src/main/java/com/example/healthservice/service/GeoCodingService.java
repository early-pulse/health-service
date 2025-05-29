package com.example.healthservice.service;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

// GeoCodingService.java
public interface GeoCodingService {
    GeoJsonPoint geocode(String address) throws IOException, InterruptedException;
}