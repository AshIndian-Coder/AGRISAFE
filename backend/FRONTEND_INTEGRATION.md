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