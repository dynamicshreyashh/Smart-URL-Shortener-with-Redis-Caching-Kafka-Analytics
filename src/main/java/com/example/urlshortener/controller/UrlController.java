package com.example.urlshortener.controller;

import com.example.urlshortener.analytics.AnalyticsService;
import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.dto.MyLinkResponse;
import com.example.urlshortener.dto.ShortenRequest;
import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private final AnalyticsService analyticsService;

    @PostMapping("/shorten")
    public ShortenResponse shorten(Authentication authentication,
                                   @Valid @RequestBody ShortenRequest request) {
        return urlShortenerService.shortenUrl(authentication.getName(), request);
    }

    @GetMapping("/my-links")
    public List<MyLinkResponse> myLinks(Authentication authentication) {
        return urlShortenerService.getMyLinks(authentication.getName());
    }

    @GetMapping("/analytics/{shortCode}")
    public AnalyticsResponse analytics(@PathVariable String shortCode) {
        return analyticsService.getAnalytics(shortCode);
    }
}
