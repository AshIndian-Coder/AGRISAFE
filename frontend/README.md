# AgroTrace Portal — Flutter Frontend

**Farm-to-Shelf Traceability Grid.** A pure-Flutter frontend for the
[Agro Trace backend](https://github.com/Faahad-Shaikh/temp) (Spring Boot ·
blockchain agricultural supply-chain traceability, SIH 2026), with a UI
faithful to the original web portal: the five-step wizard rail, NODE role
cards, dynamic per-role authentication, 6-digit security PIN pad, strict
zero-state boards, action consoles with tamper-evident receipts, and the
zero raw-identifier privacy charter.

Verified against the backend contract: `flutter analyze` → **0 issues**,
`flutter test` → **13/13 passing** (envelope parsing, error mapping, JWT
rotation, login, GPS query params, UI smoke test).

---

## 1 · Quick start

Web is enabled (see §7 for the Chrome deep-link previews).

### Android / iOS

```bash
cd agrotrace_portal
flutter pub get
flutter run            # pick your device / emulator
```

### Chrome (visual check on desktop)

```bash
cd agrotrace_portal
flutter pub get
flutter run -d chrome          # debug build, hot reload
```

### Native desktop (Windows / macOS / Linux)

Enable once:

```bash
flutter config --enable-windows-desktop --enable-macos-desktop --enable-linux-desktop
```

Toolchains: Windows → Visual Studio 2022 with *Desktop development with C++*;
macOS → Xcode; Linux → `clang cmake ninja-build pkg-config libgtk-3-dev`.
Then simply:

```bash
flutter run -d windows   # or -d macos / -d linux
```

All six platforms (android, ios, web, windows, macos, linux) are scaffolded
in this repo, and the Linux desktop build is CI-verified here.

or serve a production build:

```bash
flutter build web
# any static server, e.g.:
python3 -m http.server 8090 --directory build/web
# then open http://localhost:8090
```

Notes for Chrome:

* On web the default API base URL is `http://localhost:8080/api/v1`
  (Android emulator keeps `http://10.0.2.2:8080/api/v1`). Change it any time
  in ⚙ settings.
* The UI renders fully even with **no backend** — boards show their
  zero-state + a connection banner. To actually call the API from a browser,
  your Spring backend must allow CORS from the web origin (Android/iOS need
  no such config). The screens were verified in headless Chrome against a
  contract-faithful mock (see `screenshots/` in the workspace).

Requirements: Flutter 3.24+ (verified on **3.24.5 and 3.47.0** / latest stable).
Android, iOS, web and desktop platform folders are included.
Inter + JetBrains Mono are bundled as local font assets (OFL) — no runtime
fetching, works offline, no package-version drift.

> First paint is instant — Inter + JetBrains Mono ship inside the app
> (`assets/fonts/`), no download needed.

## 2 · Point it at your backend

Start the Agro Trace backend per its README (dev profile seeds demo users):

```bash
java -jar target/agro-trace-backend-1.0.0-SNAPSHOT.jar \
  --DB_USERNAME=... --DB_PASSWORD=... --spring.profiles.active=dev
```

Then set the API base URL in the app: **Role Selection → ⚙ settings**, or
rely on the default. Per-platform defaults for `http://localhost:8080/api/v1`:

| You run the app on…          | Base URL to use                          |
|------------------------------|------------------------------------------|
| Android emulator             | `http://10.0.2.2:8080/api/v1` (default)  |
| Physical device              | `http://<your-machine-LAN-IP>:8080/api/v1` |
| iOS simulator                | `http://localhost:8080/api/v1`           |

The settings screen has a live **HEALTH CHECK** button that pings
`/actuator/health`.

Android notes (already configured in `android/app/src/main/AndroidManifest.xml`):
`INTERNET` permission + `usesCleartextTraffic=true` (the dev backend is plain
HTTP — remove once it is behind TLS).

## 3 · Prototype logins (dev profile seeder)

All demo users share **PIN `123456`**. The app pre-fills the right identity
on each role's Dynamic Auth screen:

| Role in app             | Login identity       | Backend role                |
|-------------------------|----------------------|-----------------------------|
| 🌾 Farmer / Producer     | `AADHAR-DEMO-FARMER` | `ROLE_FARMER`               |
| 🏭 Receiving Agent       | `PF-COL-DEMO`        | `ROLE_COLLECTING_AGENT`     |
| 🚚 Middleman / Aggregator| `PF-SUP-DEMO`        | `ROLE_SUPPLIER`             |
| 🏪 Retailer / Business   | `AADHAR-DEMO-RET`    | `ROLE_RETAILER`             |
| 🛡️ FSSAI Inspector       | `PF-AG-DEMO`         | `ROLE_GOVERNMENT_EMPLOYEE`  |

Registration also works (`NEW REGISTRATION` toggle) — prototype mode accepts
any 6-digit OTP; use seeded Aadhaar reference tokens like `AADHAR-REF-001`.
FSSAI inspector accounts are provision-only (sign-in), matching the backend.

## 4 · The flow ↔ backend mapping

**Step 1 · Role Selection** — five NODE cards + public consumer gate +
privacy charter.

**Step 2 · Dynamic Auth** — role-specific credential forms (Producer Registry
Verification, Employer & Facility Verification, Trade Licence Verification,
Business Entity Verification, Officer Credential Verification). Per the zero
raw-identifier charter, Aadhaar inputs are verify-only; only registry
reference IDs are sent to the backend.

**Step 3 · Security PIN** — 6-digit pad →
`POST /auth/login` or `POST /auth/{farmer|pf|retailer}/register`.
Handles `PIN_INVALID`, `PIN_LOCKED`, lockout messaging. On app resume the PIN
gate re-authenticates (“6-digit PIN re-entry on every resume”).

**Steps 4–5 · Zero-State Board + Action Console** — per role:

| Role      | Board data                                  | Console actions |
|-----------|---------------------------------------------|-----------------|
| Farmer    | `GET /farmer/lots`, `/farmer/complaints`, `/products` | Create Farm Batch → `POST /farmer/lots`; delete CREATED lots; file complaint → `POST /farmer/complaints` |
| Agent     | `GET /agents/lots/available`                | Scan & Receive Batch → `POST /agents/lots/{id}/accept` (GPS + qrId); lot trace → `GET /lots/{id}/trace` |
| Aggregator| `GET /suppliers/assignments`                | Dispatch transfer → `POST /suppliers/packages/{id}/receive` (GPS), package lookup → `GET /suppliers/lots/{id}/packages` |
| Retailer  | `GET /retailers/bundles`                    | Stock & Mint QR → `POST /retailers/bundles/{id}/receive` (GPS + qrId) |
| Inspector | `GET /government/flags`, `/government/complaints` | Investigate lot → `GET /government/lots/{id}/full-history`; resolve flag → `POST /government/flags/{id}/resolve` |

**Public gate** — Consumer QR verification → `GET /public/products/{qrToken}`
(+ `/trace`), rendering VERIFIED / NOT VERIFIED / RECALLED verdict cards.

Cross-cutting behaviour:

* All responses are unwrapped from the backend `ApiResponse` envelope;
  error envelopes surface as banners with `code`, message and `traceId`.
* JWTs attach as `Authorization: Bearer`; on 401 the client rotates the
  token once via `POST /auth/refresh?refreshToken=…` and retries.
* Operational scans carry GPS (`latitude`, `longitude` query params) —
  fields are pre-filled with Mumbai coordinates so demos work instantly.
* QR replay errors (`409 QR_ALREADY_CONSUMED`) display the backend's
  incident message verbatim.

## 5 · Project layout

```
lib/
├── main.dart                     # bootstrap: config → session → client → api
├── core/
│   ├── app_theme.dart            # portal design tokens (gov navy, saffron, leaf)
│   ├── api_client.dart           # HTTP layer: envelope, JWT, refresh rotation
│   ├── deps.dart                 # service locator
│   ├── models.dart               # defensive DTO models (Lot, Bundle, Flag, …)
│   └── roles.dart                # NODE-01…05 ↔ backend role mapping
├── services/agro_api.dart        # one method per backend endpoint
└── ui/
    ├── app.dart                  # theme + resume/PIN-gate routing
    ├── components/               # header, step rail, PIN pad, KPI tiles,
    │                             # zero-state, receipts, QR glyph, timeline
    └── screens/                  # role selection, dynamic auth, PIN,
                                  # dashboard (+consoles), consumer verify,
                                  # settings
test/                             # models, api-client contract, widget smoke
```

## 6 · Design fidelity

Tokens lifted from the web portal's stylesheet: gov navy `#0D3B66` /
`#082A4A` / `#12497D`, saffron `#FF9933`, leaf green `#1B5E20` / `#2E7D32`,
slate background `#F4F6F9`, Inter for prose, JetBrains Mono for ledger data,
tricolor hairline under the masthead, mono status stamps and hash-chained
receipt cards with a deterministic ledger signature + QR glyph.
