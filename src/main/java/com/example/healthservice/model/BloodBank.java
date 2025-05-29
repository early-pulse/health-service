package com.example.healthservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.healthservice.enums.BloodType;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bloodBanks")
public class BloodBank {
    @Id
    private String id;

    private String name;
    private String address;
    private String phone;
    private String email;
    private boolean active;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    // Map of blood type to quantity in units
    private Map<BloodType, Integer> bloodInventory;

    // Operating hours
    private String openingTime;
    private String closingTime;
} 