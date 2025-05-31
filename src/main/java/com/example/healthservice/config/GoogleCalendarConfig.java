package com.example.healthservice.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.calendar.credentials-file.path}")
    private String credentialsFilePath;

    @Value("${google.calendar.application-name}")
    private String applicationName;

    @Value("${GOOGLE_CREDENTIALS_JSON:}")
    private String credentialsJson;

    @Bean
    public Calendar googleCalendar() throws Exception {
        InputStream credentialsStream = getCredentialsStream();
        
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
            credentials.refreshIfExpired();

            return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
        } finally {
            if (credentialsStream != null) {
                credentialsStream.close();
            }
        }
    }

    private InputStream getCredentialsStream() throws Exception {
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            // Write credentials from environment variable to file
            File credentialsFile = new File(credentialsFilePath);
            try (FileWriter writer = new FileWriter(credentialsFile)) {
                writer.write(cleanJsonString(credentialsJson));
            }
            return new FileInputStream(credentialsFile);
        }

        // Try to read from file
        File credentialsFile = new File(credentialsFilePath);
        if (credentialsFile.exists()) {
            return new FileInputStream(credentialsFile);
        }

        // Fallback to credentials file in resources
        return new ClassPathResource("credentials.json").getInputStream();
    }

    private String cleanJsonString(String json) {
        return json.trim()
            .replace("\\\"", "\"")
            .replace("\n", " ")
            .replace("\r", "");
    }
}
