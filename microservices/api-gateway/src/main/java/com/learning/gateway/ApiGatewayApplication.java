package com.learning.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * INTERVIEW CONCEPT: API Gateway (Spring Cloud Gateway)
 * ============================================================
 *
 * Why API Gateway?
 * 1. Single Entry Point: clients only need to know gateway port (9000).
 * 2. Routing: routes incoming requests to downstream services (user, order).
 * 3. Cross-Cutting Concerns: security, rate-limiting, and logging can be
 *    handled globally here instead of duplicated in every microservice.
 *
 * Under the Hood:
 * - Built on Spring WebFlux, Project Reactor, and Netty.
 * - Unlike Spring MVC, it uses non-blocking reactive event loops, allowing
 *   it to handle thousands of concurrent requests with low resource usage.
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("""
                ╔═══════════════════════════════════════════════════╗
                ║   API Gateway - STARTED on Port 9000              ║
                ║   Routing active for:                             ║
                ║     - /users/** -> user-service (Port 8081)       ║
                ║     - /auth/**  -> user-service (Port 8081)       ║
                ║     - /orders/** -> order-service (Port 8082)     ║
                ╚═══════════════════════════════════════════════════╝
                """);
    }
}
