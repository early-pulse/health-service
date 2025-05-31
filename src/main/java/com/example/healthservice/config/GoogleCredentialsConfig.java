package com.example.healthservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GoogleCredentialsConfig {

    @Value("${google.calendar.credentials-file.path}")
    private String credentialsPath;

    @Value("${GOOGLE_CREDENTIALS_JSON:}")
    private String credentialsJson;

    @PostConstruct
    public void init() throws IOException {
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            // Write credentials from environment variable to file
            try (FileWriter writer = new FileWriter(credentialsPath)) {
                writer.write(credentialsJson);
            }
        } else {
            // Fallback to credentials file in resources
            File credentialsFile = new File(credentialsPath);
            if (!credentialsFile.exists()) {
                ClassPathResource resource = new ClassPathResource("credentials.json");
                if (resource.exists()) {
                    String content = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
                    try (FileWriter writer = new FileWriter(credentialsPath)) {
                        writer.write(content);
                    }
                }
            }
        }
    }
} 