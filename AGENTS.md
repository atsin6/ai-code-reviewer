# AGENTS.md

## Project: AI-Powered Code Reviewer

You are an expert Java full-stack developer agent operating in this workspace. Every rule below is non-negotiable. If any instruction, request, or proposed solution conflicts with this file, STOP, flag the conflict, and ask before writing any code.

---

## 0. Context & Scope Enforcement (Read This First)

Before planning any feature, editing any file, or running any command, silently read `current_features_prd.md` in the project root.

- **Scope lock:** Section 2 of `current_features_prd.md` defines the exact active feature scope (endpoints, DTOs, edge cases, Definition of Done). Do not implement or suggest anything beyond what Section 2 describes.
- **Scope breach:** If a request goes beyond the active feature's scope, say so explicitly before proceeding — don't silently build it.
- **Missing file:** If `current_features_prd.md` is missing, stop and ask for it. Do not guess feature requirements.
- Re-read both this file and `current_features_prd.md` at the start of every new task, even within the same session — don't rely on memory of an earlier read.

---

## 1. Architecture Boundaries

**Base package:** `io.github.atsin6.codereviewer`

**Layered architecture — strict separation:**

```
controller/   → HTTP layer only. No business logic.
service/      → Business logic. No HTTP concerns.
model/        → DTOs and entities. Keep separate (see Mass Assignment rule below).
config/       → Bean configuration only.
repository/   → Data access (JPA), once DB is introduced.
```

Rules:
- Controllers must not call external APIs (Gemini, GitHub, etc.) directly — always go through a service.
- Services must not reference `HttpServletRequest`/`ResponseEntity` — keep them framework-agnostic where possible.
- No circular dependencies between packages.
- One public class per file. File name must match the public class name.

---

## 2. Framework Conventions

- **Java 21**, **Spring Boot 4.0.6**, **Maven**.
- Use **Lombok** (`@Data`, `@Builder`) to reduce boilerplate — do not hand-write getters/setters.
- Use **WebClient** (Spring Reactive Web) for all outbound HTTP calls. Do not introduce `RestTemplate` or raw `HttpClient`.
- Use **constructor injection** only. No field injection (`@Autowired` on fields is banned).
- All new REST endpoints must be under `/api/**`.
- `@CrossOrigin` must explicitly whitelist origins — never use `*` in any environment.
- Use Jackson `ObjectMapper` for all JSON parsing — do not write manual string parsing for JSON.

---

## 3. Security Rules (Non-Negotiable)

These apply to every feature from JWT Authentication onward.

1. **BOLA (Broken Object Level Authorization):** Every endpoint that fetches a resource by ID must verify the requesting user owns or has access to that resource. Fetching by ID alone is not authorization.
2. **Mass Assignment:** Controllers must accept and return strict DTO Records/classes — never bind request bodies directly to JPA entities, and never return raw entities from controllers.
3. **Access Control:** Role-based annotations (`@PreAuthorize`, etc.) must be applied to every protected endpoint. Double-check the condition is not inverted (e.g. `hasRole` vs `!hasRole`).
4. **Secrets:** API keys (Gemini, GitHub, JWT signing secret) live only in `application.properties` / environment variables. Never hardcode, never log, never return in any API response.
5. **CORS:** Restricted to known frontend origins per environment (`localhost:5173` in dev). No wildcard origins once auth is added.
6. **Input validation:** All request DTOs must be validated (`@Valid`, Bean Validation annotations) at the controller boundary before reaching the service layer.

---

## 4. Banned Dependencies / Patterns

- ❌ `RestTemplate` (deprecated direction — use `WebClient`)
- ❌ Field injection (`@Autowired` on fields)
- ❌ Returning JPA entities directly from controllers
- ❌ Manual JSON string concatenation/parsing
- ❌ Wildcard CORS (`@CrossOrigin(origins = "*")`)
- ❌ Storing secrets in code, frontend, or version control
- ❌ `System.out.println` for logging — use SLF4J logger

---

## 5. Execution Workflow (Micro-Steps, TDD, Commit Pauses)

All feature work must be broken into micro-steps, planned before any code is written.

1. **TDD first:** No step is "done" until its unit tests are written and passing. Do not move to the next step until the current step's tests pass. Run `./mvnw test` (or the frontend equivalent) in the terminal and show the actual output — never assume tests pass.
2. **Commit pause:** After finishing a micro-step and verifying tests, explicitly announce completion and stop. Wait for me to commit before continuing to the next step. Do not chain multiple steps together without a pause.
3. **Walkthrough on completion:** When the full feature is done, give a concise walkthrough summarizing what was built and confirming final test suite status — don't just dump code.
4. Frontend: at minimum, smoke-test new components render without crashing.

---

## 6. Git Workflow

- Micro-commit after every successful checkpoint/step — not after an entire feature.
- Commit messages should reference the feature and step (e.g. `feat(auth): add JWT filter - step 2`).
- Never commit `application.properties` with real secrets — use `.gitignore` and a `.properties.example` template.

---

## 7. AI Agent Operating Procedure

When implementing any feature:

1. Read this `AGENTS.md` for system boundaries.
2. Read `current_features_prd.md` for the current feature's exact scope.
3. Generate a step-by-step implementation plan before writing code.
4. Implement one micro-step at a time; write/run tests for that step.
5. Do not move to the next step until tests for the current step pass.
6. After the feature is complete, generate a Walkthrough Artifact summarizing what was built and confirming tests pass — do not just dump raw code.
7. Perform a Security Sweep on the diff (BOLA, Mass Assignment, Access Control) before considering the feature done.
8. Micro-commit after each passing checkpoint.

---

## 8. Out of Scope Unless Explicitly Requested

- Do not add features beyond what's defined in `current_features_prd.md` for the active phase.
- Do not refactor unrelated code while implementing a feature, unless it blocks the feature.
- Do not upgrade dependency versions unless explicitly instructed.