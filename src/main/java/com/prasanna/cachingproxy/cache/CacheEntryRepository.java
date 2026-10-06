package com.prasanna.cachingproxy.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CacheEntryRepository extends JpaRepository<CacheEntry, Long> {
    Optional<CacheEntry> findByCacheKey(String cacheKey);
}