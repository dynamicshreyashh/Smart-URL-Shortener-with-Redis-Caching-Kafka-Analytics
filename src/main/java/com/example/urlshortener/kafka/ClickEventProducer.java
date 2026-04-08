package com.example.urlshortener.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickEventProducer {

    @Value("${app.kafka.topic.click-events}")
    private String clickTopic;

    private final KafkaTemplate<String, ClickEvent> kafkaTemplate;

    public void publish(ClickEvent event) {
        kafkaTemplate.send(clickTopic, event.getShortCode(), event);
    }
}
