# Implementation Plan: Integrate Kafka and Redis

This plan details the addition of **Redis Caching** and **Apache Kafka Message Broker** to our Spring Boot User and Order Management application.

---

## User Review Required

> [!IMPORTANT]
> - **Kafka & Redis Services:** We will add Kafka and Redis to the existing `docker-compose.yml`. You will need to run `docker compose up -d` after execution to start these services.
> - **Kafka Broker Type:** We are using **KRaft (Kafka Raft) mode** for Kafka, which avoids the need for a separate Zookeeper container, keeping our resource consumption low.

---

## Proposed Changes

### 1. Infrastructure & Configurations

#### [MODIFY] [pom.xml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/pom.xml)
- Add dependencies for Redis Caching: `org.springframework.boot:spring-boot-starter-data-redis`
- Add dependencies for Apache Kafka: `org.springframework.kafka:spring-kafka`

#### [MODIFY] [application.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/resources/application.yml)
- Configure Redis connection properties (`spring.data.redis.host`, `spring.data.redis.port`, `spring.cache.type=redis`).
- Configure Kafka connection details (`spring.kafka.bootstrap-servers`, producer serializers, consumer deserializers).

#### [MODIFY] [docker-compose.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/docker-compose.yml)
- Add `redis:7-alpine` service.
- Add `confluentinc/cp-kafka:7.6.0` service in KRaft mode.
- Update `api` service to depend on both `redis` and `kafka`.

---

### 2. Redis Caching Implementation

#### [NEW] [CacheConfig.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/config/CacheConfig.java)
- Annotate with `@EnableCaching` to enable Spring Boot cache management.
- Configure cache expiration times (TTL) for Redis cache manager (e.g., 10 minutes default).

#### [MODIFY] [UserServiceImpl.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/UserServiceImpl.java)
- Annotate `getUserById` with `@Cacheable(value = "users", key = "#id")`.
- Annotate `updateUser` with `@CacheEvict(value = "users", key = "#id")`.
- Annotate `deleteUser` with `@CacheEvict(value = "users", key = "#id")`.

---

### 3. Kafka Event-Driven Messaging

#### [NEW] [KafkaConfig.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/config/KafkaConfig.java)
- Configure Kafka Topic bean (`order-events`) to auto-create the topic if it doesn't exist.

#### [NEW] [OrderEvent.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/dto/OrderEvent.java)
- DTO representing the event payload sent over Kafka (contains order ID, product name, quantity, customer email, and timestamp).

#### [NEW] [KafkaProducerService.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/KafkaProducerService.java)
- Inject `KafkaTemplate<String, Object>` to publish `OrderEvent` payloads to the `order-events` topic.

#### [NEW] [KafkaConsumerService.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/KafkaConsumerService.java)
- Annotate listener method with `@KafkaListener(topics = "order-events", groupId = "user-order-group")` to receive and log events, simulating downstream order processing.

#### [MODIFY] [OrderServiceImpl.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/OrderServiceImpl.java)
- Inject `KafkaProducerService`.
- Inside `createOrder`, publish an `OrderEvent` upon successful persistence.

---

## Verification Plan

### Automated Tests
- Run `mvn clean compile` to check that the dependencies and custom configurations build cleanly.

### Manual Verification
1. Start services: `docker compose up -d --build`
2. Register/Login to get a JWT.
3. Retrieve a User by ID (check logs to see DB SQL output). Retrieve the user a *second* time (verify **no** SQL query is printed, meaning it was served from Redis cache).
4. Update the user (verify cache is cleared).
5. Post a new Order for the user. Verify the API log streams show the Kafka producer publishing the event and the Kafka consumer log showing the message successfully consumed: `"Consumed Order Event: ..."`
