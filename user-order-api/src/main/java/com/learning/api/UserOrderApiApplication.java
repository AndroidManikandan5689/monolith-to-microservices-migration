package com.learning.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * INTERVIEW CONCEPT: @SpringBootApplication
 * ============================================================
 *
 * @SpringBootApplication is a COMPOSED annotation — it is a shortcut
 * for THREE annotations combined:
 *
 *  1. @SpringBootConfiguration
 *     - Subtype of @Configuration
 *     - Marks this class as a source of Spring bean definitions
 *     - Allows you to define @Bean methods here
 *
 *  2. @EnableAutoConfiguration
 *     - THE MAGIC OF SPRING BOOT
 *     - Scans the classpath for libraries and AUTO-CONFIGURES beans
 *     - Example: finds postgresql driver → auto-configures DataSource bean
 *     - Example: finds spring-webmvc → auto-configures DispatcherServlet
 *     - Uses META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 *       file to discover which AutoConfiguration classes to apply
 *     - Each auto-configuration is conditional:
 *       @ConditionalOnClass(DataSource.class) — only if DataSource exists on classpath
 *
 *  3. @ComponentScan
 *     - Scans the current package AND all sub-packages
 *     - Registers all @Component, @Service, @Repository, @Controller beans
 *
 * ============================================================
 * INTERVIEW: How does Spring Boot know which beans to auto-configure?
 * ============================================================
 * When your app starts:
 * 1. Spring reads all AutoConfiguration classes from spring.factories / imports
 * 2. Each AutoConfiguration has @Conditional annotations
 * 3. If conditions pass → beans are created
 * 4. If conditions fail → beans are skipped
 *
 * Example: DataSourceAutoConfiguration
 *   @ConditionalOnClass(DataSource.class)      ← Is driver on classpath?
 *   @ConditionalOnMissingBean(DataSource.class) ← Did YOU define one already?
 *   → If both pass, Spring creates a DataSource bean from your application.yml
 *
 * ============================================================
 * APPLICATION STARTUP FLOW:
 * ============================================================
 *
 *  main() called
 *    │
 *    ▼
 *  SpringApplication.run()
 *    │
 *    ├── Create ApplicationContext (IoC Container)
 *    ├── Load Environment (application.yml, env vars)
 *    ├── Run AutoConfiguration
 *    ├── Component Scan → register all beans
 *    ├── Wire dependencies (DI)
 *    ├── Run ApplicationRunner / CommandLineRunner beans
 *    ├── Start embedded Tomcat on port 8080
 *    └── Application is READY
 */
@SpringBootApplication
public class UserOrderApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserOrderApiApplication.class, args);
        System.out.println("""
                ╔═══════════════════════════════════════════════════╗
                ║   User & Order Management API - STARTED           ║
                ║   Swagger UI: http://localhost:8080/swagger-ui.html║
                ║   API Docs:   http://localhost:8080/v3/api-docs    ║
                ╚═══════════════════════════════════════════════════╝
                """);
    }
}
