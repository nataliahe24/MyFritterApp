package org.services.orders.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class TrackingCodeGenerator {

    private static final String PREFIX = "ORD";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final int RANDOM_DIGITS = 4;

    public String generateTrackingCode() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String randomPart = generateRandomDigits();
        
        return String.format("%s-%s-%s", PREFIX, datePart, randomPart);
    }

    private String generateRandomDigits() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < RANDOM_DIGITS; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
} 