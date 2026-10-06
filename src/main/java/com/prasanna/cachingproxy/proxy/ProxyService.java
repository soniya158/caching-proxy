package com.prasanna.cachingproxy.proxy;

import com.prasanna.cachingproxy.cache.CacheService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
public class ProxyService {
    private final CacheService cache;
    private final KafkaTemplate<String, ProxyEvent> kafka;

    public ProxyService(CacheService cache, KafkaTemplate<String, ProxyEvent> kafka) {
        this.cache = cache;
        this.kafka = kafka;
    }

    public ProxyResponse get(String path) {
        String key = "proxy:" + path;
        Optional<String> cached = cache.get(key);

        if (cached.isPresent()) {
            publish(key, "CACHE_HIT");
            return new ProxyResponse("REDIS", 200, cached.get());
        }

        // Demo upstream response. Replace this with WebClient in the next stage.
        String upstreamBody = "{\"path\":\"" + escape(path) + "\",\"message\":\"response from upstream\"}";
        cache.put(key, upstreamBody);
        publish(key, "CACHE_MISS_AND_STORED");

        return new ProxyResponse("UPSTREAM", 200, upstreamBody);
    }

    public void evict(String path) {
        String key = "proxy:" + path;
        cache.evict(key);
        publish(key, "CACHE_EVICTED");
    }

    private void publish(String key, String type) {
        kafka.send("cache.events", key, new ProxyEvent(key, type, Instant.now()));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}