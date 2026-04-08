package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MyLinkResponse {
    private String shortCode;
    private String originalUrl;
    private Instant createdAt;
    private Instant expiresAt;
}
