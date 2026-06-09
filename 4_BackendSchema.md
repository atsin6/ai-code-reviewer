# Backend Schema Document

## AI-Powered Code Reviewer

**Version:** 1.0

---

# 1. Overview

The backend is responsible for:

* Receiving code review requests from the React frontend
* Validating user input
* Constructing prompts for Gemini
* Communicating with Gemini API
* Processing AI responses
* Returning structured review results

---

# 2. Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| REST API | Spring Web |
| HTTP Client | WebClient (Spring Reactive Web) |
| Build Tool | Maven |
| Boilerplate Reduction | Lombok |
| AI Provider | Gemini API (gemini-2.5-flash) |

---

# 3. Package Structure

Base package: `io.github.atsin6.codereviewer`

```
src/main/java/io/github/atsin6/codereviewer/
│
├── controller/
│   └── ReviewController.java
│
├── service/
│   └── CodeReviewService.java
│
├── model/
│   ├── ReviewRequest.java
│   └── ReviewResponse.java
│
├── config/
│   └── WebClientConfig.java
│
└── CodeReviewerApplication.java

src/main/resources/
└── application.properties

src/main/resources/static/
(not used — frontend is React/Vite on separate port)
```

---

# 4. Component Responsibilities

## ReviewController

* Exposes REST endpoint `POST /api/review`
* Exposes health check `GET /api/health`
* Validates incoming request
* Calls `CodeReviewService`
* Returns `ReviewResponse` as JSON
* Annotated with `@CrossOrigin` to allow React frontend on port 5173

---

## CodeReviewService

* Reads API key from `application.properties`
* Builds structured prompt using language and code
* Calls Gemini API via `WebClient`
* Extracts text from Gemini response
* Parses JSON into `ReviewResponse`
* Returns `ReviewResponse` to controller

Key methods:

```java
ReviewResponse reviewCode(ReviewRequest request);
String buildPrompt(String language, String code);
ReviewResponse parseResponse(String aiResponseText);
```

---

## WebClientConfig

* Configures `WebClient` bean
* Sets base URL for Gemini API
* Centralizes HTTP client setup

---

# 5. API Contract

## Endpoint

```http
POST /api/review
```

### Request

```json
{
  "language": "Java",
  "code": "public class Main {}"
}
```

### Response

```json
{
  "bugs": "Potential null pointer issue.",
  "performance": "Use StringBuilder instead of string concatenation.",
  "bestPractices": "Add input validation.",
  "improvedCode": "..."
}
```

### Health Check

```http
GET /api/health
```

Response: `"Server is running!"`

---

# 6. Data Models

## ReviewRequest.java

```java
package io.github.atsin6.codereviewer.model;

@Data
public class ReviewRequest {
    private String language;
    private String code;
}
```

## ReviewResponse.java

```java
package io.github.atsin6.codereviewer.model;

@Data
@Builder
public class ReviewResponse {
    private String bugs;
    private String performance;
    private String bestPractices;
    private String improvedCode;
}
```

---

# 7. Gemini API Integration

## Endpoint

```
POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}
```

## Request Body

```json
{
  "contents": [
    {
      "parts": [
        { "text": "your prompt here" }
      ]
    }
  ]
}
```

## Response Extraction

```
response → candidates[0] → content → parts[0] → text
```

---

# 8. Prompt Structure

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

# 9. application.properties

```properties
gemini.api.key=YOUR_API_KEY_HERE
gemini.model=gemini-2.5-flash
gemini.base-url=https://generativelanguage.googleapis.com
```

---

# 10. Validation Rules

## Language

* Required
* Allowed: Java, JavaScript, Python, C++

## Code

* Required
* Not blank
* Maximum 5000 characters

## Failure Response

```http
400 Bad Request
```

```json
{
  "message": "Code cannot be empty"
}
```

---

# 11. Error Handling

| Scenario | HTTP Status | Response |
|----------|-------------|----------|
| Empty/invalid input | 400 | `{ "message": "..." }` |
| Gemini API failure | 500 | `{ "message": "Failed to generate review" }` |
| JSON parse failure | 500 | `{ "message": "Failed to parse AI response" }` |

---

# 12. Security Considerations

* API key stored only in `application.properties` (backend only)
* Never exposed to frontend or browser
* `@CrossOrigin` restricted to `localhost:5173` in development

---

# 13. MVP Summary

The backend consists of:

* 1 REST Controller
* 1 Service Layer
* 2 Model classes
* 1 WebClient Configuration
* Gemini API integration

Demonstrates:

* REST API design
* Layered architecture
* Third-party API integration
* Input validation
* Error handling
* Structured JSON responses
