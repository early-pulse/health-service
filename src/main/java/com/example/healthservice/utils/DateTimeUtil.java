package com.example.healthservice.utils;

import com.google.api.client.util.DateTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

    // Example formatter (adjust pattern as needed)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Parse a date-time string into a LocalDateTime.
     * @param dateTimeStr the date-time string.
     * @return the LocalDateTime object.
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }

    /**
     * Convert a Java LocalDateTime to Google API DateTime (UTC).
     * @param localDateTime the local date-time.
     * @return a Google DateTime object (UTC).
     */
    public static DateTime toDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        Date date = Date.from(zdt.toInstant());
        return new DateTime(date);
    }

    // Convert Google DateTime to LocalDateTime (UTC)
    public static LocalDateTime toLocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofEpochSecond(dateTime.getValue() / 1000, 0, ZoneOffset.UTC);
    }
}
