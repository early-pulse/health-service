package com.example.healthservice.service.impl;

import com.example.healthservice.dto.NominatimLocation;
import com.example.healthservice.service.GeoCodingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpHeaders;
import com.google.common.util.concurrent.RateLimiter;

import org.apache.http.HttpEntity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

@Service
public class GeoCodingServiceImpl implements GeoCodingService {
    private static final String NOMINATIM_URL =
        "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=";
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0); // 1 permit per second

    private final RestTemplate restTemplate;

    public GeoCodingServiceImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public GeoJsonPoint geocode(String address) throws IOException, InterruptedException {
        rateLimiter.acquire();

        // Encode fully to include commas
       String encodedAddress = UriUtils.encode(address, StandardCharsets.UTF_8);
       String url = NOMINATIM_URL + encodedAddress;
        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build request with User-Agent header (Nominatim requires identifying user agent)
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "JavaNominatimExample/1.0 (at95891154@gmail.com)")
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        try {
           ObjectMapper mapper = new ObjectMapper();
           NominatimLocation[] results = mapper.readValue(response.body(), NominatimLocation[].class);

           if (results == null || results.length == 0) {
               throw new IllegalArgumentException("No location found for address: " + address);
           }

           double lat = Double.parseDouble(results[0].getLat());
           double lon = Double.parseDouble(results[0].getLon());

           return new GeoJsonPoint(lon, lat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse geocoding result", e);
        }
    }

}