# Implementation Plan: API Gateway & Distributed SAGA Pattern

This plan details the transition of our monolithic user-order API into a distributed microservices architecture featuring a **Spring Cloud API Gateway** and a **Choreography-based SAGA Pattern** using Apache Kafka.

---

## 🧠 Architectural Overview

```
                      Client Request
                            │
                            ▼
               ┌─────────────────────────┐
               │    Spring Cloud Gateway │ (Port: 9000)
               └─────────────────────────┘
                 /                     \
         Route: /users                 Route: /orders
               /                         \
              ▼                           ▼
      ┌──────────────┐             ┌──────────────┐
      │ user-service │             │order-service │
      │ (Port: 8081) │             │ (Port: 8082) │
      └──────────────┘             └──────────────┘
              ▲                            │
              │                            │
              │   Kafka Event-Driven Bus   │
              │    (order-events topic)    │
              └────────────────────────────┘
```

### 1. Spring Cloud API Gateway (Port 9000)
- Single entry point routing requests:
  - `/users/**` -> `user-service`
  - `/orders/**` -> `order-service`
- Implemented using **Spring Cloud Gateway** (non-blocking Netty engine).

### 2. Choreography SAGA Pattern
To manage distributed transactions without global database locks:
1. **Order Initiation:** `order-service` receives request, saves the Order in a `PENDING` state, and publishes `OrderCreatedEvent` to Kafka.
2. **User Validation:** `user-service` consumes `OrderCreatedEvent`, verifies if the user exists, and allocates limit:
   - **Case A (Success):** Emits `UserValidatedEvent` to Kafka.
   - **Case B (Failure):** Emits `UserValidationFailedEvent` to Kafka.
3. **SAGA Resolution:** `order-service` consumes validation events:
   - If `UserValidatedEvent` -> Update status to `CONFIRMED`.
   - If `UserValidationFailedEvent` -> Trigger **Compensating Transaction** (set status to `CANCELLED`).

---

## Proposed Folder Restructure

We will organize the code into three distinct services inside `/Users/fssdeveloper/Desktop/Manikandan/FullStack/IV`:
```
user-order-microservices/
├── api-gateway/         ← Spring Cloud Gateway
├── user-service/        ← User profile CRUD + SAGA verification logic
└── order-service/       ← Order placement + SAGA orchestrating logic
```

---

## Proposed Changes

### 1. API Gateway Service (`api-gateway`)
- **Dependencies:** `org.springframework.cloud:spring-cloud-starter-gateway`
- **Properties:** Map routes to `http://user-service:8081` and `http://order-service:8082`.

### 2. User Service (`user-service`)
- Migrate existing User entity, repository, service, and controller logic.
- Add Kafka listener to consume `OrderCreatedEvent`.
- Implement validation rules (e.g. check user exists).
- Publish `UserValidatedEvent` or `UserValidationFailedEvent`.

### 3. Order Service (`order-service`)
- Migrate existing Order entity, repository, service, and controller logic.
- Add status field (`PENDING`, `CONFIRMED`, `CANCELLED`) to `Order`.
- Publish `OrderCreatedEvent` on order creation.
- Add Kafka listener to consume validation events and update order status.

### 4. Infrastructure & Docker Compose
- Define three separate builds in `docker-compose.yml`.
- Define two separate databases: `userdb` and `orderdb` (real-world separation of concerns!).

---

## Verification Plan

### Manual Verification
1. Start services: `docker compose up -d --build`
2. **Standard Route Validation:**
   - Send GET to Gateway: `http://localhost:9000/users` -> should route to `user-service`.
   - Send GET to Gateway: `http://localhost:9000/orders` -> should route to `order-service`.
3. **SAGA Success Flow:**
   - Create a user (e.g. ID 1).
   - Place an order for User ID 1 through the gateway.
   - Check order status via gateway (`GET /orders/{id}`). It should transit: `PENDING` -> `CONFIRMED`.
4. **SAGA Compensating (Rollback) Flow:**
   - Place an order for User ID 999 (non-existent user).
   - Check order status. It should transition: `PENDING` -> `CANCELLED` (due to validation failure callback).
