package com.example.urlshortener.kafka;

import com.example.urlshortener.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "${app.kafka.topic.click-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ClickEvent event) {
        try {
            analyticsService.persistEvent(event);
        } catch (Exception e) {
            log.error("Failed to persist click event for code={}", event.getShortCode(), e);
        }
    }
}
