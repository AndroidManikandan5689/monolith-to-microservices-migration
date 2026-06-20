# 🏗️ Spring Boot Mastery — Project Walkthrough

## Project: User & Order Management REST API

---

## ✅ PHASE 1 — Foundation (COMPLETE)

### Concepts Taught
| Concept | Status |
|---------|--------|
| IoC (Inversion of Control) | ✅ |
| Dependency Injection | ✅ |
| Spring Container & Bean Lifecycle | ✅ |
| Bean Scopes (Singleton, Prototype, etc.) | ✅ |
| @SpringBootApplication (composed annotation) | ✅ |
| Auto-Configuration mechanism | ✅ |
| Maven Starters | ✅ |
| Docker: Image, Container, Volume, Network | ✅ |
| HikariCP Connection Pooling | ✅ |
| JPA vs Hibernate vs Spring Data JPA | ✅ |
| ddl-auto options | ✅ |
| application.yml Environment Abstraction | ✅ |

### Files Created
| File | Purpose |
|------|---------|
| [pom.xml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/pom.xml) | Maven dependencies |
| [UserOrderApiApplication.java](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/java/com/learning/api/UserOrderApiApplication.java) | Entry point |
| [application.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/resources/application.yml) | Configuration |
| [docker-compose.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/docker-compose.yml) | PostgreSQL + pgAdmin |

### How to start PostgreSQL
```bash
cd user-order-api
docker compose up -d postgres pgadmin
# pgAdmin: http://localhost:5050 (admin@learning.com / admin123)
# PostgreSQL: localhost:5432/userorderdb
```

---

## 🔜 PHASE 2 — User CRUD APIs (NEXT)

### To be built:
- [ ] User Entity (`@Entity`, `@Table`, JPA annotations)
- [ ] UserRepository (`JpaRepository`)
- [ ] UserDTO (request/response)
- [ ] UserService (`@Service`, `@Transactional`)
- [ ] UserController (`@RestController`, all 5 endpoints)

---

## 🔜 PHASE 3 — Validation & Exception Handling
## 🔜 PHASE 4 — Order APIs + Relationships
## 🔜 PHASE 5 — JWT Authentication
## 🔜 PHASE 6 — Full Docker
## 🔜 PHASE 7 — Testing (JUnit 5 + Mockito + Testcontainers)
## 🔜 PHASE 8 — Performance & Interview Topics
