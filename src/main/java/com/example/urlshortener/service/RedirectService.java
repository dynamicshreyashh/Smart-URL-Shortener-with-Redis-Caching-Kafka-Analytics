package com.example.urlshortener.service;

import com.example.urlshortener.exception.ApiException;
import com.example.urlshortener.kafka.ClickEvent;
import com.example.urlshortener.kafka.ClickEventProducer;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final StringRedisTemplate redisTemplate;
    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventProducer clickEventProducer;

    public String resolveOriginalUrl(String shortCode, HttpServletRequest request) {
        String cacheKey = "url:" + shortCode;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        ShortUrl shortUrl = null;
        String originalUrl = cached;

        if (originalUrl == null) {
            shortUrl = shortUrlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ApiException("Short URL not found"));
            validateNotExpired(shortUrl);
            originalUrl = shortUrl.getOriginalUrl();
            redisTemplate.opsForValue().set(cacheKey, originalUrl);
        } else {
            shortUrl = shortUrlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ApiException("Short URL not found"));
            validateNotExpired(shortUrl);
        }

        publishClickEvent(shortCode, request);
        incrementCounter(shortCode);

        return originalUrl;
    }

    private void incrementCounter(String shortCode) {
        redisTemplate.opsForValue().increment("ctr:" + shortCode);
    }

    private void validateNotExpired(ShortUrl shortUrl) {
        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Short URL has expired");
        }
    }

    private void publishClickEvent(String shortCode, HttpServletRequest request) {
        ClickEvent event = ClickEvent.builder()
                .shortCode(shortCode)
                .timestamp(Instant.now())
                .ipAddress(extractIp(request))
                .country("UNKNOWN")
                .deviceType(detectDeviceType(request.getHeader("User-Agent")))
                .referrer(request.getHeader("Referer"))
                .build();
        clickEventProducer.publish(event);
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "UNKNOWN";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "MOBILE";
        }
        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "TABLET";
        }
        return "DESKTOP";
    }
}
