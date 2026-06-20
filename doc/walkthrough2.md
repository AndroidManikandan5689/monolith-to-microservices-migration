# 🏗️ Spring Boot Mastery — Project Walkthrough

## Project: User & Order Management REST API

---

## ✅ PHASE 1 — Foundation (COMPLETE)
## ✅ PHASE 2 — User CRUD APIs (COMPLETE)
## ✅ PHASE 3 — Validation & Exception Handling (COMPLETE)
## ✅ PHASE 4 — Order CRUD APIs & Entity Relationships (COMPLETE)
## ✅ PHASE 5 — JWT Authentication & Security (COMPLETE)
## ✅ PHASE 6 — Dockerization (COMPLETE)
## ✅ PHASE 7 — Testing Suite (COMPLETE)
## ✅ PHASE 8 — Performance & Interview Topics (COMPLETE)

---

## ✅ PHASE 9 — Redis Caching & Apache Kafka Integration (COMPLETE)

We have successfully integrated a production-grade Redis cache layer and an event-driven Kafka publisher/listener broker system to handle high-performance lookups and decoupled downstream event notifications.

### 🧠 System Architecture

```
                                    ┌───────────────┐
                                    │  Redis Cache  │ (JSON Serialization)
                                    └───────────────┘
                                       ▲         │
                           (Write-Through / Evict)
                                       │         ▼ (Read hit)
  HTTP Request ──> Controller ───> Service ───> Database (PostgreSQL)
                                      │
                                      ▼ (Emit Event asynchronously)
                                ┌───────────┐
                                │   Kafka   │ ──> [order-events] Topic
                                └───────────┘          │
                                                       ▼
                                                ┌─────────────┐
                                                │ Consumer    │ (Logs order payload)
                                                └─────────────┘
```

### Concepts Taught
| Concept | Status |
|---------|--------|
| Custom Redis Cache configuration (JSON Serializer vs JDK Binary serialization) | ✅ |
| Spring Cache Abstractions (`@Cacheable`, `@CacheEvict` key mappings) | ✅ |
| Apache Kafka in KRaft mode container orchestration | ✅ |
| Partitioning and message routing using partition keys (order sequence guarantee) | ✅ |
| Kafka producer callback events processing (`CompletableFuture` / `whenComplete`) | ✅ |
| Decoupled messaging using `@KafkaListener` and consumer groups | ✅ |
| Context isolation strategies for testing with external boundaries | ✅ |

### Files Created/Modified
| File | Action | Path |
|------|--------|------|
| **pom.xml** | [MODIFY] | [pom.xml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/pom.xml) |
| **application.yml** | [MODIFY] | [application.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/resources/application.yml) |
| **docker-compose.yml** | [MODIFY] | [docker-compose.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/docker-compose.yml) |
| **Redis Cache Config** | [NEW] | [CacheConfig.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/config/CacheConfig.java) |
| **Kafka Config** | [NEW] | [KafkaConfig.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/config/KafkaConfig.java) |
| **Order Event DTO** | [NEW] | [OrderEvent.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/dto/OrderEvent.java) |
| **Kafka Producer** | [NEW] | [KafkaProducerService.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/KafkaProducerService.java) |
| **Kafka Consumer** | [NEW] | [KafkaConsumerService.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/KafkaConsumerService.java) |
| **User Service Impl** | [MODIFY] | [UserServiceImpl.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/UserServiceImpl.java) |
| **Order Service Impl** | [MODIFY] | [OrderServiceImpl.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/service/OrderServiceImpl.java) |
| **User Integration Test** | [MODIFY] | [UserIntegrationTest.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/test/java/com/learning/api/integration/UserIntegrationTest.java) |

---

## 🏃 Verification & Testing Strategy

Since the files have been fully written, you can run the following instructions to observe the caching and message brokering in action:

1. **Rebuild and Start Containers:**
   In your terminal, boot up the new Redis and Kafka containers:
   ```bash
   cd user-order-api
   docker compose up -d --build
   ```

2. **Verify Containers are healthy:**
   ```bash
   docker compose ps
   ```
   *Verify that `userorder-redis` and `userorder-kafka` are running.*

3. **Observe Service Logs:**
   Open a separate terminal pane and stream the logs:
   ```bash
   docker compose logs -f api
   ```

4. **Verify Redis Cache (GET User):**
   - Perform a `GET /api/users/{id}` request using Swagger or Curl.
   - **First execution:** In the log stream, you will see:
     `>>> [DB QUERY] Fetching user 1 from database...`
     And Hibernate SQL output.
   - **Second execution:** Execute the same request. You will observe **no** log output, and the return response is served directly from Redis in milliseconds!
   - **Update/Delete User:** Execute a `PUT` or `DELETE` request. Observe that the cache is evicted. The next `GET` request will access the database again.

5. **Verify Kafka Event Broker (POST Order):**
   - Execute a `POST /api/orders` request to create a new order.
   - Check the console log stream. You will see:
     - **Producer sends event:**
       `>>> [KAFKA PRODUCER] Publishing event to topic 'order-events': ...`
     - **Producer callback confirmation:**
       `>>> [KAFKA PRODUCER] Message successfully published. Partition: 0, Offset: 0`
     - **Consumer listener captures event:**
       `>>> [KAFKA CONSUMER] Message successfully consumed from 'order-events': ...`

---

## 🎓 Phase 9 — Interview Q&A Bank

### ❓ Q1: Why is `@CacheEvict` preferred over `@CachePut` during database updates?
> **Answer:**
> * `@CachePut` forces the method response to be serialized and updated inside Redis. While this saves a database query, it can cause problems if the updated object structure varies from the queried layout, or if the update method returns a partial representation or a different DTO.
> * `@CacheEvict` simply invalidates/deletes the cached key from Redis. On the next GET request, the service is forced to query the database, ensuring clean, fresh, and consistent data is stored in the cache.

### ❓ Q2: What is the significance of the partition key when publishing to a Kafka topic?
> **Answer:**
> * If a producer sends messages without a key (null key), Kafka distributes the messages round-robin across partitions. In this case, message order guarantees are lost.
> * If a producer provides a key (e.g., `orderId.toString()`), Kafka hashes the key and routes the message to a specific partition based on the hash: `partition = hash(key) % total_partitions`.
> * This guarantees that **all messages sharing the same key are written to the same partition**, preserving the exact sequence of events (FIFO order) for that entity.

### ❓ Q3: How do you handle serialization errors in Kafka when receiving bad payloads?
> **Answer:**
> * If a listener receives a malformed payload that cannot be deserialized, standard deserializers throw a `SerializationException` which causes the listener container to fail and stop consuming.
> * **Best Practice:** Use Spring's `ErrorHandlingDeserializer`. It catches the exception, wraps the failed payload, and passes it to the listener as a wrapper object or routes it to a **Dead Letter Topic (DLT)** for isolation and review.
