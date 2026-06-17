# current_features_prd.md

## Purpose
This file tracks (1) what is already built — the current working baseline — and (2) the exact spec for the **next feature to implement**. Update Section 2 each time you start a new feature from the roadmap.

---

## 1. Current Baseline (Already Implemented — MVP)

### What exists today
- React + Vite frontend (`localhost:5173`) — language selector, code textarea, review button, results display, loading/error states.
- Spring Boot 4.0.6 / Java 21 backend (`localhost:8080`), package `io.github.atsin6.codereviewer`.
- Single endpoint: `POST /api/review` — accepts `{ language, code }`, returns `{ bugs, performance, bestPractices, improvedCode }`.
- `GET /api/health` — returns `"Server is running!"`.
- Gemini API integration (`gemini-2.5-flash`) via `WebClient`.
- Backend validates `language` (Java/JavaScript/Python/C++) and `code` (non-blank, max 5000 chars).
- No authentication, no database, no persistence — every request is stateless and anonymous.
- `@CrossOrigin` restricted to `localhost:5173`.

### Explicitly NOT present yet
Authentication, review history, database, GitHub integration, file upload, syntax highlighting editor, multiple AI providers, Docker, rate limiting, team collaboration.

---

## 2. Active Feature Spec: JWT Authentication

> This is the next feature to build (Phase 1.1). Replace this section's contents when moving to the next roadmap item.

### Goal
Add user signup/login so requests can be tied to an identity, without yet touching review history or DB-backed features beyond a `User` table.

### In Scope
- `User` entity: `id`, `email` (unique), `passwordHash`, `createdAt`.
- `POST /api/auth/register` — accepts `{ email, password }`, creates user, returns JWT.
- `POST /api/auth/login` — accepts `{ email, password }`, validates credentials, returns JWT.
- JWT issued as a signed token (HS256), short-lived access token (e.g. 1 hour expiry).
- `JwtAuthFilter` — validates `Authorization: Bearer <token>` header on protected routes.
- `POST /api/review` becomes a protected route — requires valid JWT.
- `GET /api/health` remains public.
- Passwords hashed with BCrypt — never stored or logged in plaintext.
- Frontend: login/register forms, store JWT (in memory or secure storage — not `localStorage` for production-grade handling), attach `Authorization` header to `/api/review` calls.

### Explicitly Out of Scope (for this feature)
- Review history / database persistence of reviews (next feature).
- Password reset / email verification flows.
- OAuth / social login.
- Refresh tokens (can be a fast-follow; not required for MVP of this feature).
- Role-based permissions beyond a single default "user" role.

### Edge Cases the AI Must Handle
- Duplicate email on register → `409 Conflict`, message `"Email already registered"`.
- Invalid credentials on login → `401 Unauthorized`, generic message `"Invalid email or password"` (do not reveal which field was wrong).
- Missing/expired/malformed JWT on protected route → `401 Unauthorized`.
- Empty/malformed request body on register or login → `400 Bad Request`.
- Password minimum length (e.g. 8 chars) enforced server-side, not just client-side.

### API Contract

**Register**
```http
POST /api/auth/register
Content-Type: application/json

{ "email": "user@example.com", "password": "secret123" }
```
Response `201`:
```json
{ "token": "eyJ...", "email": "user@example.com" }
```

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{ "email": "user@example.com", "password": "secret123" }
```
Response `200`:
```json
{ "token": "eyJ...", "email": "user@example.com" }
```

**Protected review call**
```http
POST /api/review
Authorization: Bearer eyJ...
Content-Type: application/json

{ "language": "Java", "code": "..." }
```
Unauthorized response `401`:
```json
{ "message": "Invalid or missing authentication token" }
```

### New Package Additions
```
model/
  ├── User.java
  ├── AuthRequest.java
  └── AuthResponse.java
repository/
  └── UserRepository.java
service/
  └── AuthService.java
security/
  ├── JwtUtil.java
  ├── JwtAuthFilter.java
  └── SecurityConfig.java
controller/
  └── AuthController.java
```

### Definition of Done
- [ ] Register and login endpoints working, tested via curl/Postman.
- [ ] `/api/review` rejects requests without a valid JWT.
- [ ] Passwords confirmed hashed in DB (manually inspect, never logged).
- [ ] Unit tests for `AuthService` (register, login, duplicate email, bad password) passing.
- [ ] Security sweep done: no entity exposed directly in controller responses, no plaintext secrets, CORS still restricted.
- [ ] Frontend can register, log in, and successfully call `/api/review` with the token attached.