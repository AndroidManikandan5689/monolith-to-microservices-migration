package com.learning.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("""
                ╔═══════════════════════════════════════════════════╗
                ║   Order Service - STARTED on Port 8082            ║
                ║   Database: orderdb                               ║
                ║   Kafka listening: user-validation-events         ║
                ╚═══════════════════════════════════════════════════╝
                """);
    }
}
