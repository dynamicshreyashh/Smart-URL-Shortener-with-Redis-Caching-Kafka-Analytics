package com.example.urlshortener.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickEvent {
    private String shortCode;
    private Instant timestamp;
    private String ipAddress;
    private String country;
    private String deviceType;
    private String referrer;
}
