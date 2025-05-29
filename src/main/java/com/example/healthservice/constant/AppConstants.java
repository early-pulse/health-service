package com.example.healthservice.constant;

public class AppConstants {
    public static final String APPOINTMENT_TOPIC = "appointments";
    public static final String NOTIFICATION_TOPIC = "notifications";
    public static final String GOOGLE_CALENDAR_ID = "primary"; // or your calendar ID
    public static final String GOOGLE_CREDENTIALS_FILE_PATH = "/path/to/service-account-credentials.json";
    public static final String CALENDAR_ID = "primary"; // or your calendar ID
    public static final String APPLICATION_NAME = "AppointmentService";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    // Google Maps API Key (replace with your actual API key or load from environment)
    public static final String GOOGLE_MAPS_API_KEY = "<YOUR_API_KEY_HERE>";

    // URL for Google Maps Geocoding API (free tier endpoint)
    public static final String GOOGLE_MAPS_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    // Role constants
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Base URL for Nominatim search (we will append parameters)
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search";
    // Custom User-Agent as required by Nominatim usage policy:contentReference[oaicite:4]{index=4}
    private static final String USER_AGENT = "HealthBookingApp/1.0 (contact@example.com)";
}
