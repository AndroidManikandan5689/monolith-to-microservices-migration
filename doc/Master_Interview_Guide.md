# Full Stack Java & Microservices Project: Master Interview Guide

This document serves as a comprehensive guide summarizing the evolution of the project from Phase 1 to Phase 10. It is designed to help you confidently explain your project, its architecture, design decisions, and technical implementations during Full Stack Java, Spring Boot, and Microservices interviews.

---

## 1. Project Overview and Business Objectives
**Overview:** An e-commerce backend system managing users and orders. The project started as a single monolithic Spring Boot application and evolved into a highly scalable, distributed microservices architecture. 
**Business Objective:** To provide a resilient, scalable backend for user management and order processing, ensuring data consistency across distributed systems while handling high traffic efficiently.

---

## 2. Technical Architecture and Design Patterns

### Architecture Evolution
*   **Phases 1-9 (Monolith):** A single Spring Boot application containing both User and Order domains, sharing a single PostgreSQL database.
*   **Phase 10 (Microservices):** Domain-driven design splitting the monolith into independent `user-service` and `order-service`, fronted by an `api-gateway`.

### Core Design Patterns
*   **API Gateway Pattern:** A single entry point for all clients, routing traffic to downstream services.
*   **Database-per-Service Pattern:** `user-service` and `order-service` each have their own isolated database to ensure loose coupling.
*   **SAGA Pattern (Choreography):** Managing distributed transactions across services using event-driven communication without a central orchestrator.
*   **Event-Driven Architecture:** Utilizing Apache Kafka for asynchronous inter-service communication.
*   **DTO (Data Transfer Object) Pattern:** Decoupling database entities from API request/response payloads.
*   **Stateless Security:** Using JSON Web Tokens (JWT) for secure, stateless API authentication.

---

## 3. Features Implemented by Phase

### Phase 1-8: The Monolith Foundation
*   **RESTful APIs:** Full CRUD operations for Users and Orders.
*   **Data Validation:** Integrated Hibernate Validator (`@Valid`, `@NotNull`, etc.) for robust input validation.
*   **Global Exception Handling:** Centralized error handling using `@ControllerAdvice` and `@ExceptionHandler` returning structured error responses.
*   **Security:** Spring Security integrated with JWT for stateless user authentication.
*   **Testing:** Integration testing using **Testcontainers** to spin up isolated PostgreSQL instances for tests.
*   **Containerization:** Wrote Dockerfiles to containerize the Spring Boot application.

### Phase 9: Caching & Event Messaging
*   **Redis Caching:** Implemented `@Cacheable` and `@CacheEvict` to cache frequently accessed user profiles and product data, reducing database hits.
*   **Apache Kafka (KRaft Mode):** Integrated Kafka to broadcast domain events asynchronously.

### Phase 10: Microservices & SAGA
*   **Decomposition:** Split the application into two independent Spring Boot applications (`user-service`, `order-service`).
*   **Spring Cloud Gateway:** Added a reactive API Gateway using Netty to route `/api/users/**` and `/api/orders/**`.
*   **Choreography SAGA:** Replaced foreign key constraints with event-driven data consistency (detailed below).
*   **Docker Orchestration:** Created a unified `docker-compose.yml` to spin up Gateway, Microservices, PostgreSQL, Redis, and Kafka in a single network.

---

## 4. Database Design and API Integration

*   **Monolithic Phase:** Relational design. `User` entity had a `@OneToMany` relationship with `Order` entity, managed by Hibernate/JPA foreign keys.
*   **Microservices Phase:** 
    *   **Isolation:** The database was split into `user_db` and `order_db`.
    *   **Loose Coupling:** The `@ManyToOne` relationship was removed. The `order-service` now stores a primitive `Long userId` rather than a `User` entity.
    *   **API Integration:** The Gateway exposes `localhost:9000`. Requests are automatically proxied to `localhost:8081` (User) or `localhost:8082` (Order).

---

## 5. Microservices Communication Flow

### Synchronous (Client to System)
1.  Client sends request to API Gateway (`http://localhost:9000/api/orders`).
2.  Gateway evaluates the path predicate and proxies the HTTP request synchronously to `order-service`.
3.  `order-service` returns the HTTP response back through the Gateway to the client.

### Asynchronous (Service to Service)
Services do not call each other via REST (no Feign/RestTemplate). Instead, they communicate asynchronously via Kafka topics (`order-events`, `user-validation-events`), ensuring loose coupling and high availability.

---

## 6. Distributed Transactions (SAGA Choreography)
In a microservices architecture, a single business action (placing an order) spans multiple databases. We use the **SAGA Choreography pattern** to ensure data consistency:

1.  **Local Transaction 1:** Client calls `POST /api/orders`. `order-service` saves the order in `PENDING` state to `order_db`.
2.  **Event Publish:** `order-service` publishes `OrderCreatedEvent` to Kafka.
3.  **Local Transaction 2:** `user-service` consumes the event, checks `user_db` to see if the user exists and is active.
4.  **Event Publish:** `user-service` publishes either `UserValidatedEvent` or `UserValidationFailedEvent` to Kafka.
5.  **Local Transaction 3:** `order-service` consumes the validation event and updates the order status to `CONFIRMED` or `CANCELLED`.

---

## 7. Security Implementation
*   **API Gateway:** Acts as a dumb router. It does not perform JWT validation, allowing services to manage their own security boundaries.
*   **Microservices:** `user-service` contains the `JwtAuthenticationFilter`. Every request intercepted by the service is validated. If the `Authorization` header is missing or the JWT is invalid, a `403 Forbidden` or `401 Unauthorized` is returned.

---

## 8. Deployment and CI/CD Process
*   **Local Deployment:** A single `docker-compose up -d` command spins up the entire cluster.
*   **Database Initialization:** A custom `init.sh` script is mounted into the PostgreSQL container at `/docker-entrypoint-initdb.d/` to automatically provision multiple databases (`user_db`, `order_db`) on startup.
*   **Environment Variables:** Configuration is injected dynamically via Docker Compose (e.g., `SPRING_DATASOURCE_URL`, `KAFKA_BOOTSTRAP_SERVERS`).

---

## 9. Real-Time Use Cases & Examples

*   **High-Traffic Sales Event:** If thousands of users check their profiles, **Redis caching** handles the read load, protecting the PostgreSQL database from crashing.
*   **Service Failure Resiliency:** If the `user-service` goes down, the `order-service` can still accept orders (saving them as `PENDING`). Once `user-service` comes back online, it processes the backlog of Kafka events, eventually confirming the orders.

---

## 10. Challenges Faced and Solutions Implemented

**Challenge 1: Managing multiple databases in local Docker development.**
*   *Problem:* The official PostgreSQL Docker image only creates one database specified by `POSTGRES_DB` by default.
*   *Solution:* Wrote a custom shell script (`init.sh`) and mounted it to `/docker-entrypoint-initdb.d/` to run `CREATE DATABASE` commands automatically upon container initialization.

**Challenge 2: Maintaining Data Consistency without Foreign Keys.**
*   *Problem:* Breaking the monolith meant losing ACID transactions across Users and Orders.
*   *Solution:* Implemented SAGA choreography using Kafka. This ensures eventual consistency while preventing service coupling.

**Challenge 3: Security Context in Microservices.**
*   *Problem:* Determining where to validate JWT tokens.
*   *Solution:* Kept the API Gateway lightweight. Pushed JWT validation down to the individual services to maintain true zero-trust boundaries between microservices.

---

## 11. Common Interview Questions & Answers

**Q: Why did you migrate from a Monolith to Microservices?**
> A: As the application grew, the monolith became harder to scale and deploy. By splitting User and Order domains, we can now scale them independently. For example, during a sale, we might need 5 instances of `order-service` but only 2 of `user-service`.

**Q: How does your API Gateway work?**
> A: We use Spring Cloud Gateway built on Project Reactor and Netty. It uses route predicates (matching paths like `/api/users/**`) to proxy traffic asynchronously without blocking threads.

**Q: What is the SAGA pattern, and why did you choose Choreography?**
> A: SAGA is a sequence of local transactions where each transaction updates data within a single service and publishes an event to trigger the next transaction. We chose Choreography over Orchestration because it requires no central controller, reducing the single point of failure and keeping services completely decoupled.

**Q: How do you handle exceptions in your REST APIs?**
> A: We use Spring's `@ControllerAdvice` to intercept exceptions (like `EntityNotFoundException` or `MethodArgumentNotValidException`) globally and return a standardized JSON error response containing the timestamp, HTTP status, and specific error message.

**Q: Why did you use Testcontainers?**
> A: H2 in-memory databases often behave differently than actual production databases (PostgreSQL). Testcontainers allows our integration tests to run against a real, disposable Dockerized PostgreSQL database, ensuring high confidence in our code.
