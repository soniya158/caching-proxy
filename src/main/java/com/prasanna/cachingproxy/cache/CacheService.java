package com.prasanna.cachingproxy.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;

@Service
public class CacheService {
    private static final Duration TTL = Duration.ofMinutes(5);
    private final StringRedisTemplate redis;

    public CacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redis.opsForValue().get(key));
    }

    public void put(String key, String value) {
        redis.opsForValue().set(key, value, TTL);
    }

    public void evict(String key) {
        redis.delete(key);
    }
}