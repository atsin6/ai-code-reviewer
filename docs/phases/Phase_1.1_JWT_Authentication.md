# Implementation Plan: Phase 1.1 - JWT Authentication

## Goal Description
Implement stateless JWT authentication for the Code Reviewer application. This phase introduces user registration, login, and secure route protection for the `/api/review` endpoint, ensuring that code reviews are tied to authenticated identities.

## Proposed Changes

---

### Database & Entities

#### [NEW] User.java
- Create JPA entity mapped to the `users` table.
- Fields: `id`, `email` (unique), `passwordHash`, `createdAt`.

#### [NEW] AuthRequest.java & AuthResponse.java
- DTOs for `POST /api/auth/register` and `POST /api/auth/login`.

---

### Security & JWT Configuration

#### [NEW] JwtUtil.java
- Utility class to generate, parse, and validate JWT tokens using `io.jsonwebtoken` (jjwt).
- Secret and expiration injected via environment variables.

#### [NEW] JwtAuthFilter.java
- Filter to intercept all HTTP requests, extract the `Bearer` token from the `Authorization` header, validate it, and set the `SecurityContext`.

#### [NEW] SecurityConfig.java
- Configures Spring Security filter chain.
- Permits `/api/auth/**` and `/api/health`.
- Protects `/api/review/**`.
- Configures BCrypt password encoder.

---

### Service & Controller Layer

#### [NEW] UserRepository.java
- Spring Data JPA repository for user data access.

#### [NEW] AuthService.java
- Handles business logic for hashing passwords, saving users, and generating tokens.

#### [NEW] AuthController.java
- Exposes `POST /api/auth/register` and `POST /api/auth/login`.

---

### Frontend Integration

#### [NEW] AuthForm.jsx
- React component handling login and registration tabs.

#### [MODIFY] api.js
- Implements `login(email, password)` and `register(email, password)`.
- Updates `reviewCode()` to attach the `Authorization: Bearer <token>` header to outgoing requests.

#### [MODIFY] App.jsx
- Adds authentication state to conditionally render the AuthForm versus the code review interface.

---

## Verification Plan

- [x] Unit Tests for JwtUtil and AuthService.
- [x] Integration Tests for SecurityConfig to verify route protection.
- [x] Frontend smoke tests verifying token storage and header injection.
