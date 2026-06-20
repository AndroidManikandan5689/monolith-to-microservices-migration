# E-Commerce Microservices Architecture 🚀

An advanced e-commerce backend system demonstrating a complete migration from a Spring Boot Monolith to a distributed, highly scalable Microservices Architecture.

## 📌 Project Overview
This repository showcases the evolution of an e-commerce backend built with Java and Spring Boot. The primary goal of this project is to implement an **Event-Driven Microservices Architecture** capable of handling distributed transactions safely and efficiently, while remaining robust under heavy load.

## 🏗 Architecture & Technologies
*   **Java 21 & Spring Boot 3** (REST APIs, Validation, Spring Data JPA)
*   **Spring Cloud Gateway** (Reactive API routing via Netty)
*   **Apache Kafka** (Event-Driven Architecture & asynchronous messaging in KRaft mode)
*   **Redis** (Data caching for high-performance reads)
*   **PostgreSQL** (Database-per-service architecture)
*   **Spring Security & JWT** (Stateless authentication)
*   **Docker & Docker Compose** (Container orchestration)
*   **Testcontainers** (Integration testing against real databases)

## ⚙️ Key System Designs
*   **Database-per-Service:** Independent PostgreSQL databases (`user_db` and `order_db`) initialized automatically via custom shell scripts to ensure loose coupling.
*   **SAGA Pattern (Choreography):** Implemented distributed transactions using Apache Kafka to maintain eventual data consistency without relying on distributed locks or two-phase commits.
*   **Stateless Security:** The API Gateway acts as a dumb router, while downstream microservices perform independent, true zero-trust JWT validation.

## 🚀 How to Run Locally

### Prerequisites
*   Docker and Docker Compose installed on your machine.
*   Make sure ports `9000`, `8081`, `8082`, `5433`, `6379`, and `9092` are free.

### Build and Start the Cluster
To build and start the entire microservices cluster (API Gateway, User Service, Order Service, PostgreSQL databases, Redis, and Kafka), navigate to the `microservices` directory and run:

```bash
cd microservices
docker compose up -d --build
```

The system will be accessible via the API Gateway at `http://localhost:9000`.

## 📖 Master Interview Guide
For a deep dive into the architectural decisions, migration phases, microservices communication flow, and challenges overcome during development, please refer to the [Master Interview Guide](doc/Master_Interview_Guide.md) located in the `doc/` directory. 

---
*Built as a comprehensive technical showcase of modern Java Full Stack & Microservices capabilities.*
