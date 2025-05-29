package com.example.healthservice.config;

import com.example.healthservice.constant.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.maps.GeoApiContext;

@Configuration
public class GoogleMapsConfig {

    @Bean
    public GeoApiContext geoApiContext() {
        return new GeoApiContext.Builder()
                .apiKey(AppConstants.GOOGLE_MAPS_API_KEY)
                .build();
    }
}
