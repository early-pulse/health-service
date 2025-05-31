package com.example.healthservice.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.calendar.credentials-file.path}")
    private String credentialsFilePath;

    @Value("${google.calendar.application-name}")
    private String applicationName;

    @Bean
    public Calendar googleCalendar() throws Exception {
        File credentialsFile = new File(credentialsFilePath);
        InputStream credentialsStream;
        
        if (credentialsFile.exists()) {
            credentialsStream = new FileInputStream(credentialsFile);
        } else {
            credentialsStream = new ClassPathResource("credentials.json").getInputStream();
        }

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
}
