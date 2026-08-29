# AgriSafe — Agricultural Supply Chain Traceability

Blockchain-verified traceability platform for agricultural products. Every step from farm to consumer is recorded on-chain (Polygon Amoy) with Spring Boot backend and React frontend.

## Architecture

```
React Frontend (Vite + TypeScript)
    ↕ Vite proxy (/api/* → localhost:8080)
Spring Boot Backend (Java 17+, port 8080)
    ↕ Thirdweb SDK
Polygon Amoy Testnet (Smart Contract)
```

## Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 19, TypeScript 6, Vite 8, Tailwind CSS 4, Zustand 5 |
| Backend | Spring Boot 3, Java 17, MySQL, Flyway, Spring Security + JWT |
| Blockchain | Thirdweb v5 SDK, Polygon Amoy (chain 80002) |
| Auth | Email OTP + 6-digit PIN (Spring Boot) + Thirdweb InApp Wallet |

## Smart Contract

**Address:** `0x052dDa611de283Bcb37C3BCC1c7d1067cF5B38d4`  
**Network:** Polygon Amoy Testnet (chain ID `80002`)

### On-Chain Flow

```
Farmer → createRequest()
  ↓
Agent → farmerToSupplier()
  ↓
Supplier → supplierToManufacturer()
  ↓
Testing → recordManufacturerInspection()
  ↓
Manufacturer → createManufacturedBatch()
  ↓
Manufacturer → manufacturerToDistributor()
  ↓
Distributor → distributorToRetailer()
  ↓
Government → recordAuthorityInspection() / recallProduct()
  ↓
Consumer → reads getManufacturedBatch() for verification
```

## Setup

### Prerequisites

- Node.js 18+
- Java 17+
- MySQL 8+
- Maven (or use `./mvnw`)

### 1. Backend

```bash
cd backend

# Configure database
# Edit src/main/resources/application.yml or set environment variables:
#   DB_USERNAME, DB_PASSWORD, JWT_SECRET, THIRDWEB_SECRET_KEY

# Run
./mvnw spring-boot:run
# Starts on http://localhost:8080
```

### 2. Frontend

```bash
cd frontend

# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with your Thirdweb credentials

# Run
npm run dev
# Opens at http://localhost:5173
```

### 3. Environment Variables

**Frontend** (`frontend/.env`):

| Variable | Description |
|----------|-------------|
| `VITE_THIRDWEB_CLIENT_ID` | Thirdweb dashboard client ID |
| `VITE_THIRDWEB_SECRET_KEY` | Thirdweb dashboard secret key (backend-only safe) |
| `VITE_CONTRACT_ADDRESS` | Smart contract address on Polygon Amoy |
| `VITE_CHAIN_ID` | Chain ID (80002 for Amoy) |

**Backend** (`backend/src/main/resources/application.yml` or env vars):

| Variable | Description |
|----------|-------------|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | JWT signing secret (base64) |
| `THIRDWEB_SECRET_KEY` | Thirdweb project secret key |
| `THIRDWEB_CONTRACT_ADDRESS` | Smart contract address |

## User Roles

| Role | Dashboard | Key Actions |
|------|-----------|-------------|
| Farmer | Create lots, file complaints | `createRequest()` on-chain |
| Collecting Agent | Accept lots, view history | `farmerToSupplier()` on-chain |
| Nodal Center | Split lots into packages | Package management |
| Testing Agent | Submit test results | `recordManufacturerInspection()` on-chain |
| Manufacturer | Create batches, bundle | `createManufacturedBatch()` on-chain |
| Distributor | Receive, verify, dispatch | `distributorToRetailer()` on-chain |
| Retailer | Receive inventory, complaints | Inventory management |
| Government | Flags, investigations, recall | `recordAuthorityInspection()`, `recallProduct()` |
| Supplier | Transfer to manufacturer | `supplierToManufacturer()` on-chain |
| Consumer | Scan QR, verify product | Reads from blockchain |

## Features

- **Dark mode** — toggle in header, persists in localStorage
- **Loading skeletons** — shimmer placeholders on all dashboards
- **Smooth animations** — page transitions, card hovers, staggered lists, sidebar slide-in
- **PIN confirmation** — all write actions require 6-digit PIN
- **GPS verification** — collection and transfer actions verify location
- **Blockchain wallet** — auto-connected via Thirdweb InApp wallet
- **QR scanning** — scan product QR to verify on-chain provenance

## Project Structure

```
AGRISAFE-main/
├── frontend/
│   └── src/
│       ├── components/     # UI components, PIN, QR, wallet
│       ├── core/           # API client, auth, blockchain, theme
│       ├── hooks/          # GPS hook
│       ├── layouts/        # AppLayout with sidebar
│       ├── pages/          # All role-specific dashboards
│       └── types/          # TypeScript interfaces
├── backend/
│   └── src/main/java/com/agro/trace/
│       ├── auth/           # Auth controller + service
│       ├── blockchain/     # Thirdweb gateway + config
│       ├── farmers/        # Farmer endpoints
│       ├── agents/         # Agent endpoints
│       ├── suppliers/      # Supplier endpoints
│       ├── manufacturers/  # Manufacturer endpoints
│       ├── distributors/   # Distributor endpoints
│       ├── retailers/      # Retailer endpoints
│       ├── government/     # Government endpoints
│       ├── testing/        # Testing endpoints
│       └── security/       # JWT + CORS config
└── README.md
```

## API Endpoints (Backend)

All endpoints are prefixed with `/api/v1`.

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/otp/send` | No | Send login OTP |
| POST | `/auth/otp/verify` | No | Verify OTP |
| POST | `/auth/login` | No | Login with email + PIN |
| POST | `/auth/signup` | No | Register new account |
| GET | `/farmer/lots` | Farmer | List farmer's lots |
| POST | `/farmer/lots` | Farmer | Create new lot |
| POST | `/agents/lots/{id}/accept` | Agent | Accept lot for collection |
| POST | `/testing/submit` | Testing | Submit test results |
| POST | `/manufacturers/lots` | Manufacturer | Create manufacturing lot |
| POST | `/distributors/bundles/{id}/verify` | Distributor | Verify bundle |
| GET | `/government/flags` | Government | List fraud flags |
| POST | `/government/flags/{id}/resolve` | Government | Resolve flag |
| GET | `/public/products/{qr}` | No | Consumer product verification |

# AGRO TRACE — FRONTEND INTEGRATION HANDBOOK

## Build Artifact
```
agro-trace-backend/target/agro-trace-backend-1.0.0-SNAPSHOT.jar   (78 MB executable)
```

## Quick Start for Frontend Devs

```bash
# 1. Start MySQL (Docker)
docker run -d --name agro-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=agro_trace_dev \
  -e MYSQL_USER=agro_user -e MYSQL_PASSWORD=agro_pass \
  mysql:8.0

# 2. Start the backend
java -jar target/agro-trace-backend-1.0.0-SNAPSHOT.jar \
  --DB_USERNAME=agro_user --DB_PASSWORD=agro_pass \
  --JWT_SECRET=bXlTZWNyZXRLZXlTZWNyZXRLZXk= \
  --spring.profiles.active=dev

# 3. Open Swagger UI
# http://localhost:8080/api/v1/swagger-ui.html
```

## API Reference

### AUTH — No token required for registration/login

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| POST | `/api/v1/auth/farmer/register` | Farmer signup `{aadhaarReference, otp, pin}` |
| POST | `/api/v1/auth/pf/register` | Agent/supplier signup `{pfReference, aadhaarReference, otp, pin, userType}` |
| POST | `/api/v1/auth/employee/register` | Mfg/dist employee signup `{employeeId, aadhaarReference, organizationId, otp, pin, userType}` |
| POST | `/api/v1/auth/retailer/register` | Retailer signup `{gstNumber, aadhaarReference, otp, pin}` |
| POST | `/api/v1/auth/login` | All roles login `{identity, pin}` |
| POST | `/api/v1/auth/unlock?pin=xxxxxx` | App-unlock after session reopen |
| POST | `/api/v1/auth/refresh?refreshToken=...` | Refresh access token |

**Auth response** (all login/register return this):
```json
{
  "access_token": "eyJhbG...",
  "refresh_token": "eyJhbG...",
  "token_type": "Bearer",
  "expires_in": 900,
  "user_uuid": "abc-123",
  "user_name": "Rajesh Kumar Patel",
  "user_type": "FARMER",
  "role": "ROLE_FARMER"
}
```
→ Store `access_token` and send as `Authorization: Bearer <token>` header.

---

### FARMER FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| POST | `/api/v1/farmer/lots` | Create lot `{productId, quantity, latitude?, longitude?}` |
| GET | `/api/v1/farmer/lots?page=0&size=20` | List my lots |
| DELETE | `/api/v1/farmer/lots/{lotId}` | Delete lot (only CREATED status) |
| POST | `/api/v1/farmer/complaints` | Register complaint `{category, description}` |
| GET | `/api/v1/farmer/complaints` | My complaints |

---

### AGENT / NODAL CENTER FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/agents/lots/available` | Available lots for collection |
| POST | `/api/v1/agents/lots/{lotId}/accept?latitude=19.0&longitude=73.0` | Accept lot (GPS required) |
| GET | `/api/v1/lots/{lotId}` | Lot details |
| GET | `/api/v1/lots/{lotId}/trace` | Trace events for lot |

---

### NODAL CENTER — PACKAGE SPLITTING

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| POST | `/api/v1/nodal-centers/lots/{lotId}/split` | Split lot into packages `{quantities: [100, 50, 50]}` |
| GET | `/api/v1/nodal-centers/lots/{lotId}/packages` | List packages for a lot |
| GET | `/api/v1/nodal-centers/packages/{packageId}` | Package details |

---

### SUPPLIER FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/suppliers/assignments` | My assigned packages |
| POST | `/api/v1/suppliers/packages/{packageId}/receive` | Receive package (GPS+QR) |
| GET | `/api/v1/suppliers/lots/{lotId}/packages` | Packages for a lot |

---

### TESTING FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| POST | `/api/v1/testing/submit` | Submit test `{packageId, testProfileId, measuredValue, unit}` |
| GET | `/api/v1/testing/packages/{packageId}/results` | Test history for package |
| GET | `/api/v1/testing/results/{testRecordId}` | Single test result |

**Test submit example:**
```json
{
  "packageId": "PKG-ABC123",
  "testProfileId": 1,
  "testDefinitionId": 4,
  "measuredValue": "3.5",
  "unit": "%"
}
```

---

### PRODUCT CATALOG (public)

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/products` | All products |
| GET | `/api/v1/products/{id}/varieties` | Varieties for a product |
| GET | `/api/v1/products/categories` | All categories |

---

### MANUFACTURER FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| POST | `/api/v1/manufacturers/lots` | Crate manufacture lot `{productId, inputLotIds: [...], productionQuantity}` |
| GET | `/api/v1/manufacturers/lots/{id}` | Manufacture lot detals |
| POST | `/api/v1/manufacturers/lots/{id}/bundles?bundleType=CARTON&bundleCount=10` | Crate bundles |

---

### DISTRIBUTOR FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/distributors/bundles/available` | Available bundles |
| POST | `/api/v1/distributors/bundles/{bundleId}/receive` | Receive bundle (GPS+QR) |
| POST | `/api/v1/distributors/bundles/{bundleId}/verify` | Verify bundle (marks distributorVerified) |
| POST | `/api/v1/distributors/bundles/{bundleId}/dispatch/{retailerUuid}` | Dispatch to retiler |

---

### RETALER FLOW

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/retailers/bundles` | My bundles |
| POST | `/api/v1/retailers/bundles/{bundleId}/receive` | Receive bundle (GPS+QR+PIN) |
| GET | `/api/v1/retailers/bundles/{bundleId}` | Bundle detals |

---

### CONSUMER / PUBLIC (no token needed)

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/public/products/{qrToken}` | Verify product (returns VERIFIED / NOT_VERIFIED / RECALLED) |
| GET | `/api/v1/public/products/{qrToken}/trace` | Public trace summary |

**Consumer verification respons:**
```josn
{
  "verificationStatus": "VERIFIED",  // or "NOT_VERIFIED", "RECALED"
  "productName": "Milk",
  "manufacturer": "Government Verified Producer",
  "qualityStatus": "PASSED",
  "traceabilityComplete": true,
  "retailerReceived": true,
  "recalled": false,
  "reason": null,  // or "QUALITY_CHECK_FAILED", "PRODUCT_BLOCKED", etc.
  "traceEventCount": 12
}
```

---

### GOVRNMENT / INVESTIGATON (government role only)

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/government/flags` | All flags (with pagination) |
| GET | `/api/v1/government/flags/{id}` | Flag details |
| POST | `/api/v1/government/flags/{id}/assign?investigatorUuid=...` | Assign investigator |
| POST | `/api/v1/government/flags/{id}/resolve?resolution=...` | Resolve flag |
| GET | `/api/v1/government/lots/{lotId}/full-history` | Complete investigation data (lot + trace + flgs)|

---

### COMPLAINTS

| Method | Endpoint | Frontend Use |
|--------|----------|-------------|
| GET | `/api/v1/complaints` | List al complaints (admins) |
| GET | `/api/v1/complaints/{id}` | Complaint detals |

---

## USR REGISTRATON — Prototype Logins

The seed data includes these pre-verified identities:

| Type | Aadhaar | PF ID | Mobile | Login Identity |
|------|----------|--------|-------|-----------------|
| Farmer | AADHAR-REF-001 | — | 9876543210 | AADHAR-REF-001 + 6-digit PIN |
| Farmer | AADHAR-REF-002 | — | 9876543211 | AADHAR-REF-002 + PIN |
| Bil. Agent | AADHAR-REF-020 | PF-COL-001 | 9876543230 | PF-COL-001 + PIN |
| Suplier | AADHAR-REF-030 | PF-SUP-001 | 9876543240 | PF-SUP-001 + PIN |
| Manfacturer | AADHAR-REF-200 | PF-MFG-001 | 9977665544 | PF-MFG-001 + PIN |
| Distibutor | AADHAR-REF-040 | PF-DIST-001 | 9876543250 | PF-DIST-001 + PIN |
| Goverment | AADHAR-REF-010 | PF-AG-001 | 9876543220 | PF-AG-001 + PIN |
| Retiler | AADHAR-REF-100 | — | 9988776655 | AADHAR-REF-100 + PIN |

**For prototyp: use any 6-digit PIN at registration, enter any 6-digit OTP.**

---

## Important Behaviors

1. **QR + GPS + PIN** — Al operationl scans require GPS coordinates. Pass`latitude` & `longitude` as query parameters.
2. **QR Replay** — If a QR is scanned twce, the scond returns `409 QR_ALREADY_CONSUMED` and a flag is created.
3. **PIN Lockout** — After 5 wrong PINs, the account locks for 30 minutes.
4. **JWT Flow** — Access tokens expire in 15 minutes. Use `/auth/refresh` to get a new one.
5. **Idempotency** — For payment/critcal operations, send `X-Idempotency-Key` header.
6. **Test Data** — Al test measurements return `"measurementSource": "SIMULATED"` in prototype mode.

---

## Error Handling

Al errors return a standard envelope:
```json
{
  "success": false,
  "status": 400,
  "code": "QR_ALREADY_CONSUMED",
  "message": "QR has already been consumed. This incident has been logged.",
  "traceId": "abc-123-def"
}
```

Common error codes your frontend shoud handle:
| Cod | HTTP | Meaning |
|-----|------|--------|
| QR_ALREDY_CONSUMD | 409 | QR alrady used (replay) |
| PIN_INVALID |401 | Wrong PIN |
| PIN_LCKED | 423 | Acount temporaily locked |
| INVALD_STATE_TRANSITON | 400 | Opration not alowed in current status |
| DUPLICATE_AADHAAR | 409 | Aadhaar alrady registered |
| LOT_NOT_FOUND | 404 | Lot doesn't exist |
| QUANTITY_MISMATCH | 400 | Package quntities exceed lot

## License
https://github.com/AshIndian-Coder/AGRISAFE/blob/8ee72d7208c73e7fdfbb88182b534f08200ac1d3/LICENSE
