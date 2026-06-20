# 🏗️ Spring Boot Mastery — Project Walkthrough

## Project: User & Order Management REST API

---

## ✅ PHASE 1 — Foundation (COMPLETE)
## ... [PHASES 2-8 COMPLETE]
## ✅ PHASE 9 — Redis Caching & Apache Kafka Integration (COMPLETE)

---

## ✅ PHASE 10 — API Gateway & Distributed SAGA Pattern (COMPLETE)

We have evolved the architecture into a fully decoupled microservices pattern comprising a Netty-based Gateway and a choreography-based SAGA transaction orchestrator.

### 🧠 System Layout

```
                           Client Requests (Port 9000)
                                      │
                                      ▼
                        ┌───────────────────────────┐
                        │    Spring Cloud Gateway   │
                        └───────────────────────────┘
                           /                     \
                   Route: /users                 Route: /orders
                          /                         \
                         ▼                           ▼
                 ┌──────────────┐             ┌──────────────┐
                 │ user-service │             │order-service │
                 │ (Port: 8081) │             │ (Port: 8082) │
                 │   [userdb]   │             │  [orderdb]   │
                 └──────────────┘             └──────────────┘
                         ▲                            │
                         │    Kafka Message Broker    │
                         │    (order-events / user-   │
                         │    validation-events)      │
                         └────────────────────────────┘
```

### Concepts Taught
| Concept | Status |
|---------|--------|
| Netty event loop routing via Spring Cloud Gateway | ✅ |
| Database per Service design principles (separating User/Order DB schemas) | ✅ |
| Distributed transaction management using Choreography SAGA | ✅ |
| Compensating Transactions (rollback triggers on Kafka events) | ✅ |
| Decoupled entity foreign key relationships in separate databases | ✅ |

### Files Created/Modified
| File | Action | Path |
|------|--------|------|
| **Gateway Pom** | [NEW] | [api-gateway/pom.xml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/microservices/api-gateway/pom.xml) |
| **Gateway Yml** | [NEW] | [api-gateway/application.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/microservices/api-gateway/src/main/resources/application.yml) |
| **User Service Saga** | [NEW] | [user-service/UserSagaHandler.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/microservices/user-service/src/main/java/com/learning/user/saga/UserSagaHandler.java) |
| **Order Service Saga** | [NEW] | [order-service/OrderSagaOrchestrator.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/microservices/order-service/src/main/java/com/learning/order/saga/OrderSagaOrchestrator.java) |
| **Unified Compose** | [NEW] | [docker-compose.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/microservices/docker-compose.yml) |

---

## 🏃 Verification & Testing SAGA Distributed Flows

### 1. Bootstrap the Microservices Cluster
Launch the compose environment inside the `microservices` folder:
```bash
cd microservices
docker compose up -d --build
```

### 2. Verify Routing Predicates via Gateway (Port 9000)
- **Retrieve Users:** `GET http://localhost:9000/users`
  *Gateway intercepts `/users/**` and routes it to `user-service` on port 8081.*
- **Retrieve Orders:** `GET http://localhost:9000/orders`
  *Gateway intercepts `/orders/**` and routes it to `order-service` on port 8082.*

### 3. SAGA Success Path
- **Step 1:** Register a valid user (returns User ID `1`):
  ```bash
  curl -X POST http://localhost:9000/users \
       -H "Content-Type: application/json" \
       -d '{"name": "Robert", "email": "robert@test.com"}'
  ```
- **Step 2:** Place a new Order for User ID `1`:
  ```bash
  curl -X POST http://localhost:9000/orders \
       -H "Content-Type: application/json" \
       -d '{"productName": "Laptop", "quantity": 1, "userId": 1}'
  ```
  *This instantly returns the Order DTO showing status `PENDING`.*
- **Step 3:** Retrieve the Order:
  ```bash
  curl http://localhost:9000/orders/1
  ```
  *Under the hood, user-service validated the user and published `UserValidatedEvent`. order-service consumed it and updated the status to `CONFIRMED`. You should see `status: CONFIRMED`.*

### 4. SAGA Compensating Path (Rollback)
- **Step 1:** Attempt to place an order for User ID `999` (non-existent):
  ```bash
  curl -X POST http://localhost:9000/orders \
       -H "Content-Type: application/json" \
       -d '{"productName": "Phone", "quantity": 2, "userId": 999}'
  ```
  *Returns Order DTO with status `PENDING`.*
- **Step 2:** Wait a second and retrieve the Order status:
  ```bash
  curl http://localhost:9000/orders/2
  ```
  *Because User ID `999` doesn't exist, user-service emitted `UserValidationFailedEvent`. order-service processed the compensating event and rolled back the order status. You should see `status: CANCELLED`.*
