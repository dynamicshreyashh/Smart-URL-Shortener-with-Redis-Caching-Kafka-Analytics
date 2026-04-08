# Smart URL Shortener (Spring Boot + PostgreSQL + Redis + Kafka)

Production-ready single-service URL shortener with JWT authentication, Redis caching/rate limiting, and Kafka-based async analytics.

## Features
- JWT register/login
- Create short URLs (random or custom alias)
- Redirect endpoint
- URL expiration
- User-specific links
- Redis cache for short-code lookup
- Redis per-user rate limiting
- Kafka click event producer on redirect
- Kafka consumer to persist analytics in PostgreSQL
- Analytics endpoint with total + recent clicks

## API Endpoints
- `POST /auth/register`
- `POST /auth/login`
- `POST /api/shorten`
- `GET /{shortCode}`
- `GET /api/analytics/{shortCode}`
- `GET /api/my-links`

## Run Dependencies Quickly (Docker)
```bash
docker run --name pg-url -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=url_shortener -p 5432:5432 -d postgres:16

docker run --name redis-url -p 6379:6379 -d redis:7

docker run -d --name zookeeper -p 2181:2181 confluentinc/cp-zookeeper:7.6.1 \
  bash -c 'export ZOOKEEPER_CLIENT_PORT=2181 && /etc/confluent/docker/run'

docker run -d --name kafka -p 9092:9092 --link zookeeper:zookeeper \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.6.1
```

## Start Application
```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```

## Package
```bash
mvn clean package
```

## Notes
- Replace `app.jwt.secret` in `application.yml` for production.
- Add GeoIP integration to enrich `country` from IP if desired.
- Deploy behind reverse proxy and forward `X-Forwarded-For` correctly.
