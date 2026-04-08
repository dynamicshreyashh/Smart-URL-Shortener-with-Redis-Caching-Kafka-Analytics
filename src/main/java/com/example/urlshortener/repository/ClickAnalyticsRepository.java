package com.example.urlshortener.repository;

import com.example.urlshortener.model.ClickAnalytics;
import com.example.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {
    List<ClickAnalytics> findTop500ByShortUrlOrderByTimestampDesc(ShortUrl shortUrl);
    long countByShortUrl(ShortUrl shortUrl);
}
