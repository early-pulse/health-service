package com.example.healthservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimLocation {
    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    public NominatimLocation() {}

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }
}
