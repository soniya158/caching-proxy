package com.prasanna.cachingproxy.proxy;

import java.time.Instant;

public record ProxyEvent(
    String cacheKey,
    String eventType,
    Instant occurredAt
) {}