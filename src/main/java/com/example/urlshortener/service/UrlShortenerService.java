package com.example.urlshortener.service;

import com.example.urlshortener.dto.MyLinkResponse;
import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.exception.ApiException;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.model.User;
import com.example.urlshortener.repository.ShortUrlRepository;
import com.example.urlshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final RateLimitService rateLimitService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public ShortenResponse shortenUrl(String email, ShortenRequest request) {
        rateLimitService.assertWithinLimit(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Authenticated user not found"));

        if (request.getExpiresAt() != null && request.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Expiration must be in the future");
        }

        String shortCode = generateShortCode(request.getCustomAlias());

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(sanitizeUrl(request.getOriginalUrl()))
                .shortCode(shortCode)
                .expiresAt(request.getExpiresAt())
                .user(user)
                .build();

        ShortUrl saved = shortUrlRepository.save(shortUrl);
        cacheShortCode(saved.getShortCode(), saved.getOriginalUrl(), saved.getExpiresAt());

        return ShortenResponse.builder()
                .shortCode(saved.getShortCode())
                .shortUrl(baseUrl + "/" + saved.getShortCode())
                .originalUrl(saved.getOriginalUrl())
                .expiresAt(saved.getExpiresAt())
                .build();
    }

    public List<MyLinkResponse> getMyLinks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Authenticated user not found"));

        return shortUrlRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(link -> MyLinkResponse.builder()
                        .shortCode(link.getShortCode())
                        .originalUrl(link.getOriginalUrl())
                        .createdAt(link.getCreatedAt())
                        .expiresAt(link.getExpiresAt())
                        .build())
                .toList();
    }

    private String generateShortCode(String alias) {
        if (alias != null && !alias.isBlank()) {
            if (shortUrlRepository.existsByShortCode(alias)) {
                throw new ApiException("Custom alias is already in use");
            }
            return alias;
        }

        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (shortUrlRepository.existsByShortCode(code));
        return code;
    }

    private String sanitizeUrl(String url) {
        try {
            URI parsed = URI.create(url);
            if (parsed.getScheme() == null || parsed.getHost() == null) {
                throw new ApiException("Invalid URL");
            }
            return parsed.toString();
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid URL");
        }
    }

    private void cacheShortCode(String shortCode, String originalUrl, Instant expiresAt) {
        String key = "url:" + shortCode;
        redisTemplate.opsForValue().set(key, originalUrl);
        if (expiresAt != null) {
            long seconds = Duration.between(Instant.now(), expiresAt).getSeconds();
            if (seconds > 0) {
                redisTemplate.expire(key, Duration.ofSeconds(seconds));
            }
        } else {
            redisTemplate.expire(key, Duration.ofHours(24));
        }
    }
}
