package com.prasanna.cachingproxy.proxy;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CacheEventConsumer {
    @KafkaListener(topics = "cache.events", groupId = "cache-monitor")
    public void consume(ProxyEvent event) {
        System.out.printf("CACHE EVENT | type=%s key=%s time=%s%n",
                event.eventType(), event.cacheKey(), event.occurredAt());
    }
}