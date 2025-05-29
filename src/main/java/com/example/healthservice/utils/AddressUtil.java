package com.example.healthservice.utils;

import com.example.healthservice.exception.ResourceNotFoundException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

public class AddressUtil {

    /**
     * Converts a physical address into a GeoJsonPoint (latitude & longitude) 
     * using the Google Maps Geocoding API.
     */
    public static GeoJsonPoint getGeoJsonPointFromAddress(String address, GeoApiContext context) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results != null && results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;
                // Note: GeoJsonPoint constructor takes (x = longitude, y = latitude)
                return new GeoJsonPoint(lng, lat);
            } else {
                throw new ResourceNotFoundException("Unable to geocode address: " + address);
            }
        } catch (ApiException | InterruptedException | IOException e) {
            throw new ResourceNotFoundException("Failed to geocode address: " + e.getMessage());
        }
    }
}
