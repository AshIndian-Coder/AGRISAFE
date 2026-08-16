# Agro Trace - Blockchain Agricultural Supply Chain Traceability

> Government-grade backend for blockchain-based agricultural food supply chain traceability.
> Built for **Smart India Hackathon (SIH)** under Agricultural Food Tech and Rural Development.

## 🏗 Architecture Overview

```
Modular Monolith with Domain-Driven Design
```

The backend follows a modular monolith architecture with clear domain boundaries, designed so individual modules can be extracted into microservices when scale requires it.

### Technology Stack

- **JDK 21** (with Spring Boot 3.4)
- **Spring Framework** (Security, Data JPA, Validation, Actuator)
- **MySQL 8+** with Flyway migrations
- **JWT** authentication with Argon2id PIN hashing
- **OpenAPI/Swagger** documentation
- **Docker** & Docker Compose
- **JUnit 5** + Testcontainers

## 🚀 Quick Start

### Prerequisites

- JDK 21+
- Maven 3.9+
- MySQL 8+
- Docker (optional)

### Setup

```bash
# Clone and enter directory
cd agro-trace-backend

# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS agro_trace_dev;"

# Configure environment
cp .env.example .env
# Edit .env with your credentials

# Run with Maven
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev

# Or with Docker Compose
docker-compose up -d
```

### Access the API

- **API Base:** `http://localhost:8080/api/v1`
- **Swagger UI:** `http://localhost:8080/api/v1/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api/v1/api-docs`
- **Actuator:** `http://localhost:8080/api/v1/actuator/health`

## 📋 Core Features

### Authentication & Roles
- Farmer (Aadhaar-based registration)
- Collection/Receiving Agents (PF-based)
- Testing Agents
- Nodal Center Agents
- Supplier/Middleman
- Manufacturer Employees
- Distributor Employees
- Retailers (GST-based)
- Government Employees/Investigators
- Consumer (public verification)

### Supply Chain Workflow
```
Farmer → Agent → Nodal Center → Supplier → Manufacturer → Distributor → Retailer → Consumer
```

### Key Capabilities
- ✅ Farmer lot creation with QR
- ✅ GPS-based collection acceptance
- ✅ Package/Unit splitting at nodal centers
- ✅ Multi-hop supplier routing
- ✅ Quality testing with FSSAI standards engine
- ✅ Manufacturer lot merging with lineage preservation
- ✅ Bundle creation
- ✅ Distributor verification
- ✅ Retailer receipt with QR consumption
- ✅ Consumer product verification
- ✅ Government investigation workflow
- ✅ QR lifecycle with replay protection
- ✅ Fraud/anomaly detection
- ✅ Mock government registries (Aadhaar, PF, Employee)
- ✅ Blockchain integration boundary (Polygon Amoy)
- ✅ AI/ML integration boundary

## 🔐 Security

- **PIN Hashing:** Argon2id (via Spring Security)
- **Authentication:** JWT with refresh token rotation
- **Rate Limiting:** OTP and PIN attempt limits
- **Account Lockout:** After configurable PIN failures
- **RBAC:** Role-based access control
- **Object-level authorization**
- **GPS validation** for operational QR scanning
- **QR replay protection** with flagging
- **Audit logging** for all sensitive actions

## 📚 API Documentation

Full API documentation is available via Swagger UI when the application is running.

### Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/farmer/register` | Register as farmer |
| POST | `/auth/pf/register` | Register PF-based user |
| POST | `/auth/employee/register` | Register company employee |
| POST | `/auth/retailer/register` | Register retailer |
| POST | `/auth/login` | Login with identity + PIN |
| POST | `/farmer/lots` | Create lot (farmer) |
| GET | `/public/products/{qrToken}` | Consumer verification |
| POST | `/agents/lots/{id}/accept` | Accept lot (agent) |
| GET | `/government/flags` | View flags (govt only) |
| GET | `/government/lots/{id}/full-history` | Full investigation history |

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests (requires Docker)
mvn verify -Pintegration
```

## 🗄 Database

The database uses Flyway for schema migrations. Migration files are in `src/main/resources/db/migration/`.

Key seed data includes:
- 11 roles
- 20 mock Aadhaar identities (fictional)
- 11 mock PF records
- 15 agricultural products with varieties
- 21 test definitions
- 13 product-specific test profiles
- FSSAI reference standards for applicable products

## 🔗 Integration Boundaries

### Blockchain (Polygon Amoy Testnet)
See `blockchain/service/BlockchainGateway.java` for the integration interface.
Mock implementation: `MockBlockchainGateway.java`

### AI/ML
See `aiml/service/AiIntegrationPort.java` for the integration interface.
Mock implementation: `MockAiIntegrationPort.java`

### Government Registries
See `mockgovernment/service/GovernmentIdentityProvider.java`
Mock data in `mock_*` database tables.

## 📁 Project Structure

```
src/main/java/com/agro/trace/
├── auth/           # Authentication & Authorization
├── users/          # User management
├── organizations/  # Organization registration
├── lots/           # Lot management
├── packages/       # Package/unit splitting
├── qr/             # QR lifecycle
├── traceability/   # Trace events & history
├── testing/        # Quality testing
├── standards/      # FSSAI standards engine
├── manufacturing/  # Manufacturer lot merging
├── bundles/        # Bundle creation
├── distribution/   # Distributor operations
├── retail/         # Retailer receipt
├── consumer/       # Public verification
├── government/     # Investigation workflows
├── fraud/          # Anomaly detection & flags
├── complaints/     # Complaint management
├── products/       # Product catalog
├── pricing/        # Pricing engine
├── payments/       # Payment abstraction
├── audit/          # Audit logging
├── notifications/  # Notification abstraction
├── devices/        # Device integration
├── blockchain/     # Blockchain gateway
├── aiml/           # AI/ML integration
├── mockgovernment/ # Mock government registries
└── common/         # Shared utilities & DTOs
```

## 📄 License

This project is built for Smart India Hackathon (SIH).
All data is fictional except FSSAI reference standards which are sourced from official FSSAI regulations.