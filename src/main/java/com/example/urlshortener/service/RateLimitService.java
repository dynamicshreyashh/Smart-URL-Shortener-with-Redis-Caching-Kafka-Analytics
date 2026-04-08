package com.example.urlshortener.service;

import com.example.urlshortener.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.rate-limit.max-requests-per-minute:60}")
    private int maxRequestsPerMinute;

    public void assertWithinLimit(String userKey) {
        String key = "rl:" + userKey;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        if (count != null && count > maxRequestsPerMinute) {
            throw new ApiException("Rate limit exceeded. Try again later.");
        }
    }
}
