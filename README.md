# FinApk — Fintech Transaction Analyzer

A RESTful backend API built with Spring Boot and MySQL for managing and analyzing financial transactions. Supports JWT authentication, role-based access control, financial record management, and dashboard summary endpoints.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate 6 |
| Migration | Flyway |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Build | Maven |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8 running locally

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Durgesh25052003/FinApk.git
cd FinApk
```

### 2. Create the MySQL database

```sql
CREATE DATABASE fintech_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure environment variables

Create `src/main/resources/application.properties` with the following:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/fintech_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true

# JWT
jwt.secret=YOUR_JWT_SECRET_KEY
jwt.expiration=86400000
```

> Never commit real credentials. Use environment variables or a `.env` file in production.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

Swagger UI is available at `http://localhost:8080/swagger-ui.html`

---

## RBAC Model

Three roles are supported. Access is enforced at both the route level (`SecurityFilterChain`) and method level (`@PreAuthorize`).

| Role | Permissions |
|---|---|
| `VIEWER` | Read-only access to financial records and dashboard |
| `ANALYST` | Read access + can filter and view aggregated reports |
| `ADMIN` | Full access — create, update, delete records and manage users |

---

## API Endpoints

### Auth

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive JWT | No |

### User Management *(Admin only)*

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}/role` | Update user role |
| PUT | `/api/users/{id}/status` | Toggle active/inactive |
| DELETE | `/api/users/{id}` | Delete user |

### Financial Records

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/records` | List records (paginated + filtered) | All roles |
| GET | `/api/records/{id}` | Get single record | All roles |
| POST | `/api/records` | Create a new record | ADMIN |
| PUT | `/api/records/{id}` | Update a record | ADMIN |
| DELETE | `/api/records/{id}` | Delete a record | ADMIN |

**Filtering query params:** `?type=INCOME`, `?category=food`, `?startDate=2024-01-01&endDate=2024-12-31`, `?page=0&size=10&sort=date,desc`

### Dashboard

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/dashboard/summary` | Total income, expenses, net balance | All roles |
| GET | `/api/dashboard/category-breakdown` | Totals grouped by category | All roles |
| GET | `/api/dashboard/monthly-trend` | Monthly income/expense trend | All roles |
| GET | `/api/dashboard/recent-activity` | Recent transactions feed | All roles |

---

## Data Model

### FinancialRecord

| Field | Type | Notes |
|---|---|---|
| id | BIGINT | Auto-generated primary key |
| amount | DECIMAL(15,2) | Never use FLOAT for money |
| type | ENUM | `INCOME` or `EXPENSE` |
| category | VARCHAR(100) | e.g. food, salary, rent |
| date | DATE | Indexed for fast range queries |
| notes | TEXT | Optional description |
| deletedAt | DATETIME | Soft delete — null if active |

---

## Error Response Format

All errors follow a consistent shape:

```json
{
  "error": "UNAUTHORIZED",
  "message": "You do not have permission to perform this action",
  "status": 403,
  "timestamp": "2024-04-04T10:30:00Z"
}
```

---

## Running Tests

```bash
./mvnw test
```

Tests use an H2 in-memory database — no MySQL connection needed to run the test suite. The test config lives in `src/test/resources/application.properties` and automatically overrides the main MySQL config during test runs.

### What's tested

- **AuthControllerTest** — register and login endpoints, validates correct JWT response and rejects missing fields with 400
- **RbacControllerTest** — verifies that a VIEWER gets 403 when attempting to POST a record, and that an unauthenticated request gets 401
- **FinancialRecordTest** — repository-level tests using `@DataJpaTest`, covers record filtering by date range, type aggregation (SUM of INCOME/EXPENSE), and category grouping

---

## Assumptions & Tradeoffs

- **Roles stored as MySQL ENUM** — simple and readable. Tradeoff: adding a new role requires a schema migration. A `roles` table would be more flexible for a production system.
- **JWT is stateless** — no token revocation on logout. A token blacklist (Redis) would be needed for stricter security in production.
- **Soft delete on financial records** — records are never permanently removed via `deletedAt`, preserving audit history.
- **Flyway migrations** manage all schema changes — `ddl-auto=validate` ensures Hibernate never silently alters the schema.
- **H2 used only for tests** — keeps the test suite fast and portable without requiring a running MySQL instance.

---

## Project Structure

```
src/
├── main/
│   ├── java/com/fintech/transactionControl/
│   │   ├── auth/          # JWT filter, auth controller, token service
│   │   ├── config/        # SecurityConfig, OpenAPI config
│   │   ├── controller/    # REST controllers
│   │   ├── dto/           # Request & response DTOs
│   │   ├── entity/        # JPA entities
│   │   ├── exception/     # Global exception handler
│   │   ├── repository/    # JPA repositories + Specifications
│   │   └── service/       # Business logic
│   └── resources/
│       ├── application.properties
│       └── db/migration/  # Flyway SQL migrations
└── test/
    ├── java/com/fintech/transactionControl/
    │   ├── AuthControllerTest.java
    │   ├── RbacControllerTest.java
    │   └── FinancialRecordTest.java
    └── resources/
        └── application.properties  # H2 config for tests
```

---

## License

This project was built as part of a fintech backend screening assignment.