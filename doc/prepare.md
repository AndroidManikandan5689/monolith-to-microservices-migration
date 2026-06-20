
🏗️ Spring Boot Mastery: Production-Ready REST API
Welcome! I'll be your Senior Spring Boot Architect & Interview Mentor. We'll build this project step-by-step — no shortcuts, full understanding.

📍 PHASE 1 — STEP 1: Understanding Spring Boot Before Writing a Single Line
Before we touch code, let's build the mental model that 90% of candidates fail to explain in interviews.

🧠 CONCEPT: What is Spring Boot? Why does it exist?
The Problem with Plain Spring Framework:

In traditional Spring, you had to manually:

Configure a DispatcherServlet in web.xml
Define every bean in XML files
Configure DataSource, TransactionManager, JPA manually
Deploy to an external Tomcat server
This was boilerplate hell.

Spring Boot Solution: Convention over Configuration.

┌─────────────────────────────────────────────────────────┐
│                   SPRING ECOSYSTEM                      │
│                                                         │
│  ┌──────────────┐    ┌──────────────────────────────┐  │
│  │   SPRING     │    │       SPRING BOOT            │  │
│  │  FRAMEWORK   │    │                              │  │
│  │              │ ──>│  Auto-Configuration          │  │
│  │  IoC/DI      │    │  Embedded Tomcat             │  │
│  │  AOP         │    │  Starter POMs                │  │
│  │  MVC         │    │  Actuator                    │  │
│  │  Data        │    │  No XML config               │  │
│  └──────────────┘    └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
🧠 CONCEPT: IoC (Inversion of Control) — The Heart of Spring
Traditional approach (YOU control the flow):

java
// You create objects manually
UserService service = new UserService();
UserRepository repo = new UserRepository(new DataSource());
service.setRepository(repo);
Spring IoC (Spring controls the flow):

java
// Spring creates and wires everything for you
@Autowired
private UserService userService; // Spring injects this
Why is this powerful?

┌─────────────────────────────────────────────────┐
│              IoC CONTAINER (Spring)             │
│                                                 │
│  ┌──────────────┐    ┌──────────────────────┐  │
│  │  UserService │───>│  UserRepository      │  │
│  │  (Bean)      │    │  (Bean)              │  │
│  └──────────────┘    └──────────────────────┘  │
│          │                    │                │
│          └──────────┬─────────┘                │
│                     ▼                          │
│           ┌──────────────────┐                 │
│           │   DataSource     │                 │
│           │   (Bean)         │                 │
│           └──────────────────┘                 │
│                                                 │
│  Spring creates, wires, and manages lifecycle   │
└─────────────────────────────────────────────────┘
🎯 Interview Question: "What is IoC? What is the difference between IoC and DI?"

Answer: IoC is the principle — "don't call us, we'll call you." Spring controls object creation. DI (Dependency Injection) is the implementation mechanism of IoC — Spring injects dependencies into your classes instead of you creating them.

🧠 CONCEPT: Spring Container & Bean Lifecycle
┌──────────────────────────────────────────────────────────────┐
│                  SPRING BEAN LIFECYCLE                       │
│                                                              │
│  1. Bean Definition Read (from @Component, @Bean, XML)       │
│         │                                                    │
│         ▼                                                    │
│  2. BeanFactory instantiates the Bean (new Object())         │
│         │                                                    │
│         ▼                                                    │
│  3. Dependency Injection (@Autowired fields set)             │
│         │                                                    │
│         ▼                                                    │
│  4. @PostConstruct method called (init logic)                │
│         │                                                    │
│         ▼                                                    │
│  5. Bean is READY — used by the application                  │
│         │                                                    │
│         ▼                                                    │
│  6. @PreDestroy called (cleanup before shutdown)             │
│         │                                                    │
│         ▼                                                    │
│  7. Bean is destroyed                                        │
└──────────────────────────────────────────────────────────────┘
🧠 CONCEPT: Bean Scopes
Scope	Description	Interview Tip
Singleton (default)	One instance per Spring context	Most beans are singleton
Prototype	New instance every time requested	Use for stateful objects
Request	One per HTTP request	Web apps only
Session	One per HTTP session	Web apps only
🎯 Interview Trap: "Is @Service a singleton? What happens if it holds state?"

Yes, it's singleton by default. Holding mutable state in a singleton service is a concurrency bug — multiple threads share the same instance!

📍 PHASE 1 — STEP 2: Project Structure
Why Maven? Why this structure?
┌─────────────────────────────────────────────────────────────────┐
│              STANDARD SPRING BOOT PROJECT STRUCTURE             │
│                                                                 │
│  user-order-api/                                                │
│  ├── src/                                                       │
│  │   ├── main/                                                  │
│  │   │   ├── java/com/learning/api/                             │
│  │   │   │   ├── UserOrderApiApplication.java  ← Entry Point   │
│  │   │   │   │                                                  │
│  │   │   │   ├── config/          ← Spring Config beans        │
│  │   │   │   ├── controller/      ← HTTP layer (@RestController)│
│  │   │   │   ├── service/         ← Business logic (@Service)  │
│  │   │   │   ├── repository/      ← DB layer (@Repository)     │
│  │   │   │   ├── entity/          ← JPA entities (@Entity)     │
│  │   │   │   ├── dto/             ← Data Transfer Objects      │
│  │   │   │   ├── exception/       ← Custom exceptions          │
│  │   │   │   └── security/        ← JWT + Spring Security      │
│  │   │   │                                                      │
│  │   │   └── resources/                                         │
│  │   │       ├── application.yml  ← Config (DB, JWT, etc.)     │
│  │   │       └── db/migration/    ← Flyway scripts (optional)  │
│  │   │                                                          │
│  │   └── test/                                                  │
│  │       └── java/com/learning/api/                             │
│  │           ├── controller/      ← Controller tests            │
│  │           ├── service/         ← Service unit tests          │
│  │           └── integration/     ← Testcontainers tests        │
│  │                                                              │
│  ├── Dockerfile                                                 │
│  ├── docker-compose.yml                                         │
│  └── pom.xml                                                    │
└─────────────────────────────────────────────────────────────────┘
Why layers?

Separation of Concerns — each layer has one job
Testability — you can mock each layer independently
Maintainability — change DB without touching controllers
📍 PHASE 1 — STEP 3: Let's Create the Project
Let me generate the project structure now:

Good — empty workspace. Let me create the full project scaffold:

Now let's create the pom.xml — the heart of a Maven project:

pom.xml
Now the main application entry point:

UserOrderApiApplication.java
Now the application configuration (application.yml):

application.yml
Now let's create the Docker setup for PostgreSQL:

docker-compose.yml
Let's start PostgreSQL:

user-order-api $ docker compose up -d postgres pgadmin
zsh:1: command not found: docker
Docker isn't in the PATH in this shell. Let's continue with the code — you can run docker compose up -d manually from the user-order-api folder when Docker Desktop is open.

Now let's create the walkthrough artifact for what we've built in Phase 1:

Walkthrough
🎓 Phase 1 — Interview Q&A Bank
❓ Q1: What is the difference between @Component, @Service, @Repository, and @Controller?
Answer: All four are specializations of @Component — they all register a bean in the Spring container. The difference is semantic intent and additional behavior:

@Component — generic bean
@Service — marks business logic layer (no extra behavior, but documents intent)
@Repository — marks persistence layer + adds exception translation (converts DB-specific exceptions like SQLException to Spring's DataAccessException)
@Controller / @RestController — marks MVC controller; @RestController = @Controller + @ResponseBody on every method
❓ Q2: What is @Autowired? What are the three types of dependency injection?
Answer: @Autowired tells Spring to inject a dependency. Three types:

Field injection (@Autowired on field) — convenient but hard to test, hides dependencies
Setter injection (@Autowired on setter) — optional dependencies
Constructor injection (recommended) — mandatory dependencies, immutable, easy to test without Spring
Best practice: Always use constructor injection. Spring 4.3+ doesn't even require @Autowired on single-constructor classes.

❓ Q3: What is Auto-Configuration? How does Spring Boot decide what to configure?
Answer: Spring Boot reads AutoConfiguration.imports file in each starter jar. Each AutoConfiguration class is annotated with @Conditional annotations (@ConditionalOnClass, @ConditionalOnMissingBean, etc.). Spring evaluates these conditions — if they pass, the beans are created. Example: DataSourceAutoConfiguration only runs if DataSource.class is on the classpath AND no custom DataSource bean was defined.

🚨 Common Mistakes
Mistake	Why It's Wrong	Fix
Using ddl-auto: create in production	Drops all data on every restart	Use validate + Flyway migrations
Field injection with @Autowired	Hidden dependencies, can't test without Spring	Use constructor injection
Hardcoding secrets in application.yml	Security risk if committed to git	Use env vars or secrets manager
No healthcheck in Docker	App starts before DB is ready → crash	Add depends_on + condition: service_healthy
Singleton service holding mutable state	Thread-safety bugs	Keep services stateless
🧪 Your Mini Challenge (Answer Before Phase 2!)
Challenge: Look at the @SpringBootApplication annotation. It is a composed annotation.

What are the THREE annotations it combines?
What does @EnableAutoConfiguration actually do?
If you put @SpringBootApplication in the package com.learning.api, which packages does it scan?
What happens if you annotate a class with @Service and it has 2 constructors — does Spring know which one to use for injection? What error occurs?
Reply with your answers and I'll verify them and move to Phase 2: User Entity → Repository → Service → Controller!

📌 Summary of What's Been Built
user-order-api/
├── pom.xml                    ← All 15+ dependencies configured
├── docker-compose.yml         ← PostgreSQL + pgAdmin in Docker  
└── src/main/
    ├── java/com/learning/api/
    │   └── UserOrderApiApplication.java  ← @SpringBootApplication entry
    └── resources/
        └── application.yml    ← DB, JPA, JWT, Swagger config
To start PostgreSQL (open Docker Desktop first):

bash
cd /Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api
docker compose up -d postgres pgadmin
pgAdmin UI: http://localhost:5050 → admin@learning.com / admin123

Once you answer the mini challenge above, we move to Phase 2 where we'll build the full User CRUD layer by layer: Entity → Repository → DTO → Service → Controller — with every annotation explained in depth! 🚀