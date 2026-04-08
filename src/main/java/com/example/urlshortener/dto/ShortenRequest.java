package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.Instant;

@Data
public class ShortenRequest {
    @NotBlank
    @Pattern(regexp = "^(https?://).+", message = "URL must start with http:// or https://")
    private String originalUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$", message = "Alias must be 3-64 chars and only letters, numbers, _ or -")
    private String customAlias;

    private Instant expiresAt;
}
