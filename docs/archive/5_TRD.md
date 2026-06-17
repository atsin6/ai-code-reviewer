# Technical Requirements Document (TRD)

# AI-Powered Code Reviewer

**Version:** 1.0

---

# 1. Introduction

The AI-Powered Code Reviewer is a full-stack web application that enables developers to submit code snippets and receive AI-generated feedback using the Gemini API.

---

# 2. Technology Stack

## Frontend

| Component | Technology |
|-----------|------------|
| Framework | React |
| Build Tool | Vite |
| Styling | CSS |
| HTTP Client | Fetch API |
| Dev Port | localhost:5173 |

## Backend

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| REST API | Spring Web |
| HTTP Client | WebClient (Spring Reactive Web) |
| Build Tool | Maven |
| Boilerplate | Lombok |
| Base Package | io.github.atsin6.codereviewer |
| Dev Port | localhost:8080 |

## AI Layer

| Component | Technology |
|-----------|------------|
| AI Provider | Google Gemini |
| Model | gemini-2.5-flash |
| API Type | REST (generateContent) |

---

# 3. System Architecture

```
[User Browser]
      |
      v
[React App - Vite - :5173]
      |
      | POST /api/review
      v
[Spring Boot - :8080]
[io.github.atsin6.codereviewer]
      |
      | generateContent?key=API_KEY
      v
[Gemini API]
      |
      v
[Spring Boot parses response]
      |
      v
[React renders results]
      |
      v
[User reads review]
```

---

# 4. Project Structure

## Frontend

```
frontend/
├── src/
│   ├── components/
│   │   ├── Header.jsx
│   │   ├── LanguageSelector.jsx
│   │   ├── CodeInput.jsx
│   │   ├── ReviewButton.jsx
│   │   ├── LoadingSpinner.jsx
│   │   ├── ErrorMessage.jsx
│   │   └── ReviewResult.jsx
│   │
│   ├── services/
│   │   └── api.js
│   │
│   ├── App.jsx
│   ├── App.css
│   └── main.jsx
│
├── index.html
├── vite.config.js
└── package.json
```

## Backend

```
backend/
└── src/main/java/io/github/atsin6/codereviewer/
    ├── controller/
    │   └── ReviewController.java
    ├── service/
    │   └── CodeReviewService.java
    ├── model/
    │   ├── ReviewRequest.java
    │   └── ReviewResponse.java
    ├── config/
    │   └── WebClientConfig.java
    └── CodeReviewerApplication.java

src/main/resources/
└── application.properties
```

---

# 5. API Specification

## Review Endpoint

```http
POST /api/review
Content-Type: application/json
```

### Request Body

```json
{
  "language": "Java",
  "code": "public class Main {}"
}
```

### Success Response (200)

```json
{
  "bugs": "Potential null pointer exception.",
  "performance": "Use StringBuilder.",
  "bestPractices": "Add validation.",
  "improvedCode": "..."
}
```

### Error Response (400)

```json
{
  "message": "Code cannot be empty"
}
```

### Error Response (500)

```json
{
  "message": "Failed to generate review"
}
```

---

# 6. Functional Requirements

| ID | Requirement |
|----|-------------|
| FR-1 | User can select a programming language |
| FR-2 | User can submit code (max 5000 chars) |
| FR-3 | Backend sends code to Gemini and returns review |
| FR-4 | Frontend displays 4 review sections |
| FR-5 | Validation errors shown clearly |
| FR-6 | API errors handled gracefully |

---

# 7. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| Response time | < 10 seconds |
| Input limit | 5000 characters |
| Error handling | All failure cases handled |
| Security | API key never exposed to frontend |
| CORS | Allowed for localhost:5173 |

---

# 8. Gemini Integration

## Request Format

```
POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}
```

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

## Response Extraction Path

```
response.candidates[0].content.parts[0].text
```

## Prompt Strategy

* Instruct model to return ONLY valid JSON
* No markdown, no code fences, no extra text
* Specify language for context-aware review
* Define exact JSON schema in prompt

---

# 9. Validation Rules

## Language

* Required
* Accepted values: Java, JavaScript, Python, C++

## Code

* Required
* Not null, not blank
* Maximum: 5000 characters

---

# 10. Error Handling

| Scenario | Backend Response | Frontend Display |
|----------|-----------------|------------------|
| Empty code (frontend) | Not sent | "Please enter code" |
| Validation failure | 400 Bad Request | "Invalid request" |
| Gemini API failure | 500 Internal Server Error | "Unable to generate review" |
| Network failure | N/A | "Connection failed" |

---

# 11. CORS Configuration

Backend must allow requests from React frontend:

```java
@CrossOrigin(origins = "http://localhost:5173")
```

---

# 12. Future Enhancements

* JWT Authentication
* Review history with database
* GitHub repository integration
* File upload support
* Syntax highlighting editor (Monaco or CodeMirror)
* Multiple AI provider support
* Docker deployment
* Rate limiting

---

# 13. Technical Deliverables

* React + Vite frontend
* Spring Boot 4.0.6 backend
* Gemini API integration
* Full documentation set
* Working end-to-end demo
