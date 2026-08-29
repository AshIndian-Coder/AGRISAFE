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

## License

Private — AgriSafe Project
