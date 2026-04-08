package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AnalyticsItemResponse {
    private Instant timestamp;
    private String ipAddress;
    private String country;
    private String deviceType;
    private String referrer;
}
