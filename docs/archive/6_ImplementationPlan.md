# Implementation Plan

# AI-Powered Code Reviewer

**Version:** 1.0

---

# 1. Objective

Build a minimal but functional AI-powered code review application that allows users to submit code snippets and receive AI-generated feedback using Gemini API.

Stack: React + Vite (frontend), Spring Boot 4.0.6 / Java 21 (backend), Gemini API (AI layer).

Base package: `io.github.atsin6.codereviewer`

---

# 2. Development Strategy

Build incrementally. Each phase should produce a working state before moving to the next.

**Guiding Principle:** Build the simplest working version first, then improve.

Out of scope for MVP:

* Authentication
* Database
* GitHub integration
* Complex animations
* Deployment

---

# 3. Development Phases

```
Phase 1: Project Setup
       ↓
Phase 2: Backend Structure
       ↓
Phase 3: Gemini Integration
       ↓
Phase 4: React Frontend
       ↓
Phase 5: Frontend-Backend Integration
       ↓
Phase 6: Validation & Error Handling
       ↓
Phase 7: UI Polish
       ↓
Phase 8: Documentation
```

---

# 4. Phase 1 — Project Setup

## Goal

Initialize both projects and verify they run.

---

## Backend Setup

Spring Initializr settings:

| Setting | Value |
|---------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 4.0.6 |
| Group | io.github.atsin6 |
| Artifact | code-reviewer |
| Package name | io.github.atsin6.codereviewer |
| Packaging | Jar |
| Java | 21 |
| Dependencies | Spring Web, Spring Reactive Web, Lombok |

Expected result:

```
Backend running on http://localhost:8080
```

---

## Frontend Setup

```bash
npm create vite@latest frontend -- --template react
cd frontend
npm install
npm run dev
```

Expected result:

```
Frontend running on http://localhost:5173
```

---

## Deliverables

- [ ] Spring Boot project generated and starts successfully
- [ ] React + Vite project initialized and starts successfully
- [ ] Git repository initialized

---

# 5. Phase 2 — Backend Structure

## Goal

Create the API structure with placeholder responses before connecting Gemini.

---

## Files to Create

### `model/ReviewRequest.java`

Package: `io.github.atsin6.codereviewer.model`

Fields:

* `String language`
* `String code`

Annotations: `@Data`, `@NoArgsConstructor`

---

### `model/ReviewResponse.java`

Package: `io.github.atsin6.codereviewer.model`

Fields:

* `String bugs`
* `String performance`
* `String bestPractices`
* `String improvedCode`

Annotations: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

---

### `controller/ReviewController.java`

Package: `io.github.atsin6.codereviewer.controller`

Endpoint: `POST /api/review`

Return temporary hardcoded response:

```json
{
  "bugs": "Test",
  "performance": "Test",
  "bestPractices": "Test",
  "improvedCode": "Test"
}
```

Add: `@CrossOrigin(origins = "http://localhost:5173")`

Also add: `GET /api/health` → returns `"Server is running!"`

---

### `service/CodeReviewService.java`

Package: `io.github.atsin6.codereviewer.service`

Methods (stub for now):

```java
public ReviewResponse reviewCode(ReviewRequest request)
private String buildPrompt(String language, String code)
private ReviewResponse parseResponse(String aiText)
```

---

## Testing

Use Postman or curl:

```bash
curl -X POST http://localhost:8080/api/review \
  -H "Content-Type: application/json" \
  -d '{"language":"Java","code":"test"}'
```

Expected: hardcoded JSON response

---

## Deliverables

- [ ] Models created
- [ ] Controller endpoint working
- [ ] Postman test passing with hardcoded response

---

# 6. Phase 3 — Gemini Integration

## Goal

Replace hardcoded response with real Gemini API call.

---

## Step 1: Add API Key

`application.properties`:

```properties
gemini.api.key=YOUR_GEMINI_API_KEY
gemini.model=gemini-2.5-flash
gemini.base-url=https://generativelanguage.googleapis.com
```

Get free API key at: https://aistudio.google.com

---

## Step 2: Create WebClientConfig

`config/WebClientConfig.java`

Package: `io.github.atsin6.codereviewer.config`

Responsibilities:

* Create `WebClient` bean
* Set base URL from properties

---

## Step 3: Implement buildPrompt()

Prompt template:

```
You are a senior software engineer.

Review the following {language} code.

Return ONLY valid JSON with no extra text, no markdown, no code fences:

{
  "bugs": "",
  "performance": "",
  "bestPractices": "",
  "improvedCode": ""
}

Code:
{user_code}
```

---

## Step 4: Implement Gemini API Call

Gemini endpoint:

```
POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}
```

Request body format:

```json
{
  "contents": [
    {
      "parts": [
        { "text": "prompt here" }
      ]
    }
  ]
}
```

Response extraction:

```
candidates[0].content.parts[0].text
```

---

## Step 5: Implement parseResponse()

* Receive AI text (should be JSON string)
* Strip any accidental markdown fences if present
* Parse JSON into `ReviewResponse` using Jackson ObjectMapper
* Handle parse failure gracefully

---

## Testing

* Valid Java code → should return structured review
* Empty code → should fail validation
* Invalid API key → should return 500

---

## Deliverables

- [ ] Gemini API call working
- [ ] Real AI review returned
- [ ] Postman test with real code passing

---

# 7. Phase 4 — React Frontend

## Goal

Build the user interface in React.

---

## Component List

| Component | File | Responsibility |
|-----------|------|----------------|
| App | App.jsx | State management, layout |
| Header | Header.jsx | Title and subtitle |
| LanguageSelector | LanguageSelector.jsx | Language dropdown |
| CodeInput | CodeInput.jsx | Code textarea |
| ReviewButton | ReviewButton.jsx | Submit button |
| LoadingSpinner | LoadingSpinner.jsx | Loading state |
| ErrorMessage | ErrorMessage.jsx | Error display |
| ReviewResult | ReviewResult.jsx | Display review sections |

---

## API Service

`src/services/api.js`

Function:

```javascript
export async function reviewCode(language, code) {
  const response = await fetch("http://localhost:8080/api/review", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ language, code })
  });

  if (!response.ok) throw new Error("Review failed");
  return response.json();
}
```

---

## App State

```javascript
const [language, setLanguage] = useState("Java");
const [code, setCode] = useState("");
const [review, setReview] = useState(null);
const [loading, setLoading] = useState(false);
const [error, setError] = useState(null);
```

---

## Deliverables

- [ ] All components created
- [ ] UI renders correctly
- [ ] Form validation working (empty code blocked)

---

# 8. Phase 5 — Frontend-Backend Integration

## Goal

Connect React frontend to Spring Boot backend end-to-end.

---

## Steps

1. Ensure backend is running on port 8080
2. Ensure frontend is running on port 5173
3. Call `reviewCode()` from `api.js` on button click
4. Handle loading state during request
5. Display results on success
6. Display error message on failure

---

## Vite Proxy (Optional)

To avoid CORS issues in dev, add to `vite.config.js`:

```javascript
server: {
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

If using proxy, update `api.js` to use `/api/review` instead of full URL.

---

## Deliverables

- [ ] Full end-to-end flow working
- [ ] Results displayed correctly in browser
- [ ] Loading and error states working

---

# 9. Phase 6 — Validation & Error Handling

## Goal

Make the application robust.

---

## Backend Validation

* Code not null/blank → 400
* Code length > 5000 → 400
* Language not in allowed list → 400

---

## Frontend Validation

* Block submission if code empty
* Show inline error message

---

## API Error Handling

* Catch Gemini API errors
* Return consistent 500 response
* Show user-friendly message on frontend

---

## Deliverables

- [ ] Backend validates all inputs
- [ ] Frontend blocks empty submissions
- [ ] Error messages shown correctly

---

# 10. Phase 7 — UI Polish

## Goal

Make the application look professional.

---

## Improvements

* Consistent spacing and layout
* Card-based result sections
* Color-coded sections (bugs = red accent, performance = yellow, best practices = green)
* Responsive layout for mobile
* Monospace font for improved code block

---

## Optional (time permitting)

* Copy button for improved code section

---

## Deliverables

- [ ] Clean, professional UI
- [ ] Responsive on desktop and mobile

---

# 11. Phase 8 — Documentation

## Goal

Prepare complete submission documentation.

---

## Documents

- [ ] `1_PRD.md`
- [ ] `2_AppFlow.md`
- [ ] `3_UIUXBrief.md`
- [ ] `4_BackendSchema.md`
- [ ] `5_TRD.md`
- [ ] `6_ImplementationPlan.md`
- [ ] `README.md`

---

## README.md Contents

* Project description
* Tech stack
* How to run (backend + frontend steps)
* Architecture explanation
* Prompt engineering decisions
* Known limitations
* Future improvements

---

## Deliverables

- [ ] All docs complete
- [ ] README written
- [ ] Application demo ready

---

# 12. Testing Checklist

## Functional

- [ ] Language selection works
- [ ] Code submission works
- [ ] Gemini review generated correctly
- [ ] All 4 result sections displayed

## Validation

- [ ] Empty code blocked on frontend
- [ ] Empty code rejected on backend (400)

## Error Handling

- [ ] Invalid API key returns 500
- [ ] Frontend shows error message
- [ ] Network failure handled

## UI

- [ ] Desktop layout correct
- [ ] Mobile responsive
- [ ] Loading state shows
- [ ] Error state shows

---

# 13. Suggested Timeline

## Day 1

| Time | Task |
|------|------|
| Morning | Phase 1: Project setup (Spring Boot + React/Vite) |
| Morning | Phase 2: Backend structure + models + controller |
| Afternoon | Phase 3: Gemini integration + test with Postman |
| Evening | Phase 4: React components + basic UI |

## Day 2

| Time | Task |
|------|------|
| Morning | Phase 5: Frontend-backend integration |
| Afternoon | Phase 6: Validation + error handling |
| Afternoon | Phase 7: UI polish |
| Evening | Phase 8: Documentation + README + final testing |

---

# 14. Final Deliverables

## Source Code

* React + Vite frontend (`/frontend`)
* Spring Boot backend (`/backend`)

## Documentation

* PRD, TRD, App Flow, Backend Schema, UI/UX Brief, Implementation Plan
* README with architecture explanation

## Demo

Working application that:

1. Accepts code input and language selection
2. Sends code to Gemini via Spring Boot backend
3. Displays structured AI review in 4 sections
4. Handles errors gracefully

---

# 15. Success Criteria

- [ ] Frontend and backend communicate successfully
- [ ] Gemini integration returns real reviews
- [ ] Structured feedback displayed in UI
- [ ] All error cases handled
- [ ] Documentation complete
- [ ] Demo works end-to-end
