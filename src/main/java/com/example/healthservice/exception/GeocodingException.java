package com.example.healthservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GeocodingException extends RuntimeException {
    public GeocodingException(String message) {
        super(message);
    }
}
