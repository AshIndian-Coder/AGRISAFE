# AgriChain Backend - Spring Boot

Enterprise-grade backend platform for agricultural supply chain traceability.

## Technology Stack

- **Java 21**
- **Spring Boot 3.2**
- **MySQL 8.0**
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **Flyway** (database migrations)
- **Redis** (caching, rate limiting)
- **OpenAPI/Swagger** (documentation)
- **MapStruct** (DTO mapping)
- **Lombok**

## Quick Start

### Prerequisites

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis (optional)

### Setup

```bash
# Clone repository
git clone <repo>
cd springboot-backend

# Configure database
cp src/main/resources/application.yml src/main/resources/application-local.yml
# Edit application-local.yml with your database credentials

# Build
mvn clean install

# Run
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Environment Variables

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=agrichain
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=your-256-bit-secret-base64-encoded
REDIS_HOST=localhost
REDIS_PORT=6379
```

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/otp/request` | Request OTP |
| POST | `/api/v1/auth/otp/verify` | Verify OTP & login |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Logout |
| GET | `/api/v1/auth/sessions` | List sessions |
| DELETE | `/api/v1/auth/sessions` | Logout all |

### Batches

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/batches` | Create batch |
| GET | `/api/v1/batches` | List batches |
| GET | `/api/v1/batches/{id}` | Get batch |
| PATCH | `/api/v1/batches/{id}` | Update batch |
| POST | `/api/v1/batches/{id}/status` | Transition status |
| GET | `/api/v1/batches/{id}/timeline` | Get timeline |

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/products` | List products (public) |
| POST | `/api/v1/products` | Create product (admin) |

## Architecture

```
com.agrichain/
├── common/           # Shared code
│   ├── dto/          # API response types
│   ├── entity/       # Base entity
│   ├── enums/        # Enumerations
│   └── exception/    # Exception handling
├── security/         # JWT, authentication
├── identity/         # Users, sessions, OTP
├── organization/     # Organizations
├── farmer/           # Farmer profiles
├── farm/             # Farms
├── product/          # Product catalog
├── batch/            # Batch lifecycle
├── handover/         # Custody transfer
└── audit/            # Audit logging
```

## Batch State Machine

```
CREATED → PENDING_VERIFICATION → VERIFIED → READY_FOR_MOVEMENT
    ↓                                              ↓
RECALLED                                      IN_TRANSIT
                                                   ↓
                                              RECEIVED
                                                   ↓
                                    PROCESSING → PROCESSED
                                                   ↓
                                              WHOLESALE
                                                   ↓
                                               RETAIL
                                                   ↓
                                                SOLD
```

## Security Features

- JWT-based authentication
- Token rotation on refresh
- Session management
- Rate limiting (Bucket4j)
- Role-based access control
- Resource-level authorization
- Audit logging
- Input validation

## Database

- MySQL 8.0 with InnoDB
- UUID primary keys
- Optimistic locking (version columns)
- Soft deletes
- JSON columns for flexible data
- Flyway migrations

## Testing

```bash
# Unit tests
mvn test

# Integration tests (requires Testcontainers)
mvn verify -P integration-test

# With coverage
mvn test jacoco:report
```

## API Documentation

Swagger UI available at:
```
http://localhost:8080/api/swagger-ui.html
```

OpenAPI spec at:
```
http://localhost:8080/api/v3/api-docs
```

## Docker

```bash
# Build image
docker build -t agrichain-backend .

# Run
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_NAME=agrichain \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=... \
  agrichain-backend
```

## Health Checks

- Liveness: `GET /api/actuator/health/liveness`
- Readiness: `GET /api/actuator/health/readiness`
- Full: `GET /api/actuator/health`
