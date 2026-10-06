package com.prasanna.cachingproxy.cache;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cache_entries")
public class CacheEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String cacheKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private Instant createdAt;

    protected CacheEntry() {}

    public CacheEntry(String cacheKey, String responseBody, Integer statusCode) {
        this.cacheKey = cacheKey;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCacheKey() { return cacheKey; }
    public String getResponseBody() { return responseBody; }
    public Integer getStatusCode() { return statusCode; }
    public Instant getCreatedAt() { return createdAt; }
}