# ⚡ Caching Proxy

A Java 21 + Spring Boot backend project that demonstrates a high-performance caching proxy using **Redis**, **PostgreSQL**, and **Apache Kafka**.

The system reduces repeated upstream work by caching responses in Redis. Cache hits are served directly from Redis, while cache misses generate an upstream response and store it with a TTL. Kafka events provide an asynchronous audit stream for cache hits, misses, and evictions.

> **Status:** Starter implementation for learning and portfolio development. The upstream call is currently simulated; production features are listed in the roadmap.

## Architecture

```text
                     ┌─────────────────────┐
                     │      Client         │
                     │ Browser / Postman   │
                     └──────────┬──────────┘
                                │
                                ▼
                  ┌─────────────────────────┐
                  │   Spring Boot Proxy     │
                  │   REST API / Service    │
                  └──────────┬──────────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
              ┌───────────┐     ┌───────────┐
              │   Redis   │     │ PostgreSQL│
              │   Cache   │     │  Metadata │
              └─────┬─────┘     └───────────┘
                    │
             Cache HIT / MISS
                    │
                    ▼
              ┌─────────────┐
              │   Kafka     │
              │ cache.events│
              └──────┬──────┘
                     ▼
              Event Consumer
```

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data Redis
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Spring Boot Actuator
- Maven
- Docker Compose

## Features

- RESTful caching proxy API
- Redis cache-aside pattern
- Configurable cache TTL
- Cache hit / miss event publishing
- Kafka asynchronous event consumer
- Cache eviction endpoint
- PostgreSQL persistence foundation
- Validation and centralized exception handling
- Dockerized local infrastructure
- Actuator health and metrics endpoints

## Project Structure

```text
caching-proxy/
├── src/main/java/com/prasanna/cachingproxy/
│   ├── cache/
│   │   ├── CacheEntry.java
│   │   ├── CacheEntryRepository.java
│   │   └── CacheService.java
│   ├── common/
│   │   └── ApiExceptionHandler.java
│   ├── health/
│   │   └── HealthController.java
│   └── proxy/
│       ├── CacheEventConsumer.java
│       ├── ProxyController.java
│       ├── ProxyEvent.java
│       ├── ProxyRequest.java
│       ├── ProxyResponse.java
│       └── ProxyService.java
├── src/main/resources/
│   └── application.yml
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Prerequisites

- JDK 21+
- Maven 3.9+
- Docker Desktop / Docker Engine
- Git
- Postman or curl

## Run the Project

Start PostgreSQL, Redis and Kafka:

```bash
docker compose up -d
```

Start Spring Boot:

```bash
mvn spring-boot:run
```

Application:

```text
http://localhost:8080
```

Health:

```text
http://localhost:8080/api/health
```

Actuator health:

```text
http://localhost:8080/actuator/health
```

## API Examples

### First request — Cache MISS

```bash
curl "http://localhost:8080/api/proxy?path=/api/products/1"
```

Example:

```json
{
  "source": "UPSTREAM",
  "status": 200,
  "body": "{\"path\":\"/api/products/1\",\"message\":\"response from upstream\"}"
}
```

The response is stored in Redis.

### Second request — Cache HIT

Run the same request again:

```bash
curl "http://localhost:8080/api/proxy?path=/api/products/1"
```

The response should now report:

```json
{
  "source": "REDIS",
  "status": 200,
  "body": "{\"path\":\"/api/products/1\",\"message\":\"response from upstream\"}"
}
```

### Evict cache

```bash
curl -X DELETE "http://localhost:8080/api/proxy?path=/api/products/1"
```

The next request becomes a cache miss again.

### POST request format

```bash
curl -X POST http://localhost:8080/api/proxy/request \
  -H "Content-Type: application/json" \
  -d '{"path":"/api/products/1","method":"GET"}'
```

## Cache Flow

```text
Request
   │
   ▼
Generate cache key
   │
   ▼
Check Redis
   │
   ├── HIT ───────► Return cached response
   │                  │
   │                  └── Kafka CACHE_HIT
   │
   └── MISS ──────► Call upstream
                       │
                       ▼
                    Store Redis
                       │
                       ├── Kafka CACHE_MISS_AND_STORED
                       │
                       ▼
                  Return response
```

## Kafka Events

Topic:

```text
cache.events
```

Event types:

```text
CACHE_HIT
CACHE_MISS_AND_STORED
CACHE_EVICTED
```

Example event:

```json
{
  "cacheKey": "proxy:/api/products/1",
  "eventType": "CACHE_HIT",
  "occurredAt": "2026-10-06T10:00:00Z"
}
```

## Redis Strategy

The current implementation follows a **cache-aside** approach:

1. Check Redis.
2. If the key exists, return cached data.
3. If the key does not exist, obtain the upstream response.
4. Store the response in Redis.
5. Apply a TTL.
6. Return the response.

This avoids unnecessary repeated upstream calls for frequently requested data.

## Why PostgreSQL?

Redis is treated as the fast cache layer, not the long-term system of record.

PostgreSQL provides a durable relational persistence layer that can be expanded to store:

- Cache metadata
- Request statistics
- API configuration
- Cache policies
- Audit information

## Why Kafka?

Kafka decouples cache events from the synchronous request path.

This allows future consumers for:

- Analytics
- Monitoring
- Cache statistics
- Audit logging
- Alerting
- Request tracking

## 🛣️ Roadmap

### Phase 1 — Core Proxy
- [x] Spring Boot REST API
- [x] Redis caching
- [x] Cache TTL
- [x] Cache eviction
- [x] Kafka cache events
- [x] Docker infrastructure

### Phase 2 — Real Proxy
- [ ] Replace simulated upstream response with Spring WebClient
- [ ] Configure target upstream services
- [ ] Forward HTTP headers
- [ ] Forward query parameters
- [ ] Support GET / POST / PUT / DELETE
- [ ] Handle upstream timeouts

### Phase 3 — Production Caching
- [ ] Cache key normalization
- [ ] Cache stampede protection
- [ ] Request coalescing
- [ ] L1 + L2 caching
- [ ] Configurable TTL per endpoint
- [ ] LRU-style eviction policies
- [ ] Negative caching

### Phase 4 — Reliability
- [ ] Circuit breaker
- [ ] Retry with backoff
- [ ] Bulkhead isolation
- [ ] Kafka retry topic
- [ ] Dead-letter topic
- [ ] Idempotent event consumers
- [ ] Graceful degradation when Redis is unavailable

### Phase 5 — Security
- [ ] JWT authentication
- [ ] Role-based authorization
- [ ] API key support
- [ ] Rate limiting
- [ ] Request validation
- [ ] Security headers

### Phase 6 — Observability
- [ ] Prometheus metrics
- [ ] Grafana dashboard
- [ ] Cache hit ratio
- [ ] Request latency
- [ ] Redis latency
- [ ] Kafka consumer lag
- [ ] Distributed tracing

### Phase 7 — Performance
- [ ] JMeter / k6 load testing
- [ ] Concurrent request testing
- [ ] Cache hit/miss benchmarks
- [ ] Redis performance analysis
- [ ] Database query optimization
- [ ] Connection pool tuning

### Phase 8 — Deployment
- [ ] Docker images
- [ ] Kubernetes manifests
- [ ] Horizontal scaling
- [ ] CI/CD with GitHub Actions
- [ ] Cloud deployment

## Testing

Run:

```bash
mvn test
```

Future integration tests will use Testcontainers for PostgreSQL, Redis and Kafka.

Performance claims should only be added after running reproducible load tests and documenting the test environment and methodology.

## Known Limitations

This version is intentionally a foundation for further development.

- The upstream service is simulated.
- PostgreSQL is currently a persistence foundation; Redis is the active cache layer.
- Cache metadata is not yet synchronized with Redis.
- No authentication is implemented.
- No rate limiter is implemented.
- No distributed locking/stampede protection is implemented.
- Kafka events are demonstration events.
- Docker configuration is for local development.

## Git Commands

```bash
git init
git add .
git commit -m "Initial caching proxy implementation"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/caching-proxy.git
git push -u origin main
```

## Resume Description

**Caching Proxy | Java, Spring Boot, Redis, Kafka, PostgreSQL, Docker**

Built a high-performance caching proxy using Spring Boot and Redis with cache-aside strategy and TTL-based caching. Implemented Kafka-based asynchronous cache events, PostgreSQL persistence, REST APIs, Dockerized infrastructure, cache eviction, validation, and observability foundations.

## Author

**Prasanna Srinivasan**
