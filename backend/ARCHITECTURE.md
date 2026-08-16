# Architecture Documentation

## Modular Monolith Architecture

The backend follows a **modular monolith** pattern - a single deployable unit with strong domain boundaries. This provides:

- **Transactional consistency** across domains
- **Simplified deployment** for SIH prototype
- **Clear module separation** for future microservice extraction

## Domain Modules

```
┌─────────────────────────────────────────────────────┐
│                   API Layer (Controllers)             │
├─────────┬─────────┬─────────┬─────────┬──────────────┤
│  Auth   │ Farmer  │ Agent   │  Govt   │  Consumer     │
├─────────┴─────────┴─────────┴─────────┴──────────────┤
│                 Application Services                   │
├─────────┬─────────┬─────────┬─────────┬──────────────┤
│  Lot    │ Package │  QR     │ Testing │ Manufacturing │
│  Service│ Service │ Service │ Service │   Service     │
├─────────┼─────────┼─────────┼─────────┼──────────────┤
│ Bundle  │ Trace   │ Fraud   │ Std     │ Complaint    │
│ Service │ Service │ Service │ Engine  │  Service     │
├─────────┴─────────┴─────────┴─────────┴──────────────┤
│                  Domain Entities                       │
├─────────┬─────────┬─────────┬─────────┬──────────────┤
│  JPA    │  JPA    │  JPA    │  JPA    │    JPA       │
│ Repos   │ Entities│ Events  │  Maps   │   Projections│
├─────────┴─────────┴─────────┴─────────┴──────────────┤
│              External Integration Ports                │
├─────────┬─────────┬─────────┬─────────┬──────────────┤
│Blockchain│  AI/ML  │  Govt   │  OTP    │   Payment    │
│ Gateway  │   Port  │Registry │ Provider│   Provider   │
└─────────┴─────────┴─────────┴─────────┴──────────────┘
```

## Data Flow: Lot Creation → Consumer Verification

```
1. Farmer creates Lot
   → Database: Lot created (status=CREATED)
   → QR: Initial QR generated
   → Trace: LOT_CREATED event recorded

2. Agent accepts Lot  
   → GPS validation
   → QR consumed (replay protected)
   → Lot status → ACCEPTED
   → New QR generated for next stage
   → Payment workflow initiated (mock)

3. Nodal Center → Package splitting
   → Package IDs & QRs generated
   → Lineage recorded

4. Testing
   → Package receipt verification
   → FSSAI standards evaluation
   → Pass/Fail determination
   → Failed = QUARANTINED + FLAG

5. Manufacturer → Lots merged
   → Manufacturer Lot created
   → Parent histories preserved
   → Production tests

6. Bundles created → Distributor → Retailer
   → QR consumed at each transfer
   → Custody chain maintained

7. Consumer scans product QR
   → Verification gate checks all conditions
   → Returns VERIFIED or NOT_VERIFIED
```

## Security Architecture

```
Client → JWT Filter → SecurityContext → Method Security
   ↓                       ↓
Request ← Exception Handler ← Domain Service
```

- All endpoints require authentication except `/auth/**` and `/public/**`
- PINs hashed with Argon2id
- JWT with 15-min access + 30-day refresh tokens
- Object-level authorization in service layer
- GPS required for operational QR operations
- Rate limiting on OTP and PIN attempts

## Database Design Principles

- Strong unique constraints at DB level (not just application checks)
- UUIDs for external IDs, auto-increment for internal IDs
- `@Version` for optimistic locking
- Immutable audit/trace records (append-only)
- Flyway migrations for all schema changes
- Proper indexes on lookup columns