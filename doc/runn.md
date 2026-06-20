Ran command: `docker compose up -d postgres pgadmin`
Viewed UserOrderApiApplication.java:65-78

### 🔍 Why you received `ERR_CONNECTION_REFUSED`

You ran the following command:
```bash
docker compose up -d postgres pgadmin
```

This started **only** the PostgreSQL database container and the pgAdmin GUI container. The Spring Boot application container (`api`) is **not running** yet! That is why `localhost:8080` is refusing to connect.

---

### 🛠️ How to Fix It

To build and start all containers—including the Spring Boot app—run this command in your terminal:

```bash
docker compose up -d --build
```

#### 💡 What this command does:
1. `--build`: Triggers Docker to run our multi-stage `Dockerfile`. It compiles the Java code using Maven inside the container, packages the fat jar, and copies it to a lightweight JRE runtime container.
2. `up -d`: Runs all three services (`postgres`, `pgadmin`, and `api`) in the background.

---

### ⚠️ Important URL Adjustment (Context-Path)

Because we configured a context path in our [application.yml](file:///Users/fssdeveloper/Desktop/Manikandan/FullStack/IV/user-order-api/src/main/resources/application.yml):
```yaml
server:
  servlet:
    context-path: /api
```

All requests must be prefixed with `/api`. This means the correct URLs are:
* **Swagger UI:** `http://localhost:8080/api/swagger-ui.html` (instead of `/swagger-ui.html`)
* **API Docs JSON:** `http://localhost:8080/api/v3/api-docs`

---

### 🏃 Step-by-Step Verification

Once you run `docker compose up -d --build`, you can follow these checks:

1. **Check if all containers are running:**
   ```bash
   docker compose ps
   ```
   *You should see all three containers (`userorder-postgres`, `userorder-pgadmin`, and `userorder-api`) marked as "Up" or "Running".*

2. **Stream Spring Boot logs to make sure it started successfully:**
   ```bash
   docker compose logs -f api
   ```
   *You should see the Spring Boot banner and the console message `User & Order Management API - STARTED`.*

3. **Open in browser:**
   * Go to **[http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)** to test out registration, login, and CRUD operations directly from the Swagger UI dashboard!