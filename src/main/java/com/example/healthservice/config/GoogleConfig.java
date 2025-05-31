package com.example.healthservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Base64Utils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class GoogleConfig {

    @Value("${google.calendar.credentials-file.path}")
    private String credentialsPath;

    @Value("${GOOGLE_CREDENTIALS_BASE64:}")
    private String credentialsBase64;

    @PostConstruct
    public void init() throws IOException {
        if (credentialsBase64 != null && !credentialsBase64.isEmpty()) {
            // Decode base64 and write to file
            byte[] decodedBytes = Base64Utils.decodeFromString(credentialsBase64);
            Path path = Paths.get(credentialsPath);
            Files.createDirectories(path.getParent());
            Files.write(path, decodedBytes);
        }
    }
} 