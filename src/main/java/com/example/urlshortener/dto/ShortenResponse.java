package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ShortenResponse {
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Instant expiresAt;
}
