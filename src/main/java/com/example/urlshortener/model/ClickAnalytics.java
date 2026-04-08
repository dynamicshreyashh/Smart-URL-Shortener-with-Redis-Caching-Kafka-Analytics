package com.example.urlshortener.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "click_analytics", indexes = {
        @Index(name = "idx_analytics_short_url_id", columnList = "short_url_id"),
        @Index(name = "idx_analytics_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_url_id")
    private ShortUrl shortUrl;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(length = 64)
    private String ipAddress;

    @Column(length = 64)
    private String country;

    @Column(length = 64)
    private String deviceType;

    @Column(length = 512)
    private String referrer;
}
