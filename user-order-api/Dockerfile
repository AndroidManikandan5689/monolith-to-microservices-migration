# ============================================================
# STAGE 1: Build stage using Maven and JDK 21
# ============================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application fat-jar, bypassing unit tests (tests run in Phase 7 container)
RUN mvn clean package -DskipTests

# ============================================================
# STAGE 2: Runtime stage using a lightweight JRE
# ============================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the compiled fat jar from the builder stage
COPY --from=builder /app/target/user-order-api-1.0.0-SNAPSHOT.jar app.jar

# Expose port 8080 (documentation only, mapping is configured in compose)
EXPOSE 8080

# Run Spring Boot app with optimizations
ENTRYPOINT ["java", "-jar", "app.jar"]
