package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalyticsResponse {
    private String shortCode;
    private long totalClicks;
    private List<AnalyticsItemResponse> recentClicks;
}
