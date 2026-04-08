package com.example.urlshortener.analytics;

import com.example.urlshortener.dto.AnalyticsItemResponse;
import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.exception.ApiException;
import com.example.urlshortener.kafka.ClickEvent;
import com.example.urlshortener.model.ClickAnalytics;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.repository.ClickAnalyticsRepository;
import com.example.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickAnalyticsRepository clickAnalyticsRepository;

    public void persistEvent(ClickEvent event) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(event.getShortCode())
                .orElseThrow(() -> new ApiException("Short URL not found for analytics"));

        ClickAnalytics analytics = ClickAnalytics.builder()
                .shortUrl(shortUrl)
                .timestamp(event.getTimestamp())
                .ipAddress(event.getIpAddress())
                .country(event.getCountry())
                .deviceType(event.getDeviceType())
                .referrer(event.getReferrer())
                .build();

        clickAnalyticsRepository.save(analytics);
    }

    public AnalyticsResponse getAnalytics(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ApiException("Short URL not found"));

        long total = clickAnalyticsRepository.countByShortUrl(shortUrl);
        List<AnalyticsItemResponse> recent = clickAnalyticsRepository.findTop500ByShortUrlOrderByTimestampDesc(shortUrl)
                .stream()
                .map(item -> AnalyticsItemResponse.builder()
                        .timestamp(item.getTimestamp())
                        .ipAddress(item.getIpAddress())
                        .country(item.getCountry())
                        .deviceType(item.getDeviceType())
                        .referrer(item.getReferrer())
                        .build())
                .toList();

        return AnalyticsResponse.builder()
                .shortCode(shortCode)
                .totalClicks(total)
                .recentClicks(recent)
                .build();
    }
}
