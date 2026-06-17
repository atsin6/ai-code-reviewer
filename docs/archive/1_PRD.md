# Product Requirements Document (PRD)

## Project Title

AI-Powered Code Reviewer

## Overview

AI-Powered Code Reviewer is a web application that allows developers to paste source code and receive automated feedback using a Large Language Model (LLM).

The application analyzes submitted code and provides:

* Potential bugs and issues
* Performance improvement suggestions
* Best practice recommendations
* A cleaner or improved version of the code

The goal is to demonstrate practical integration of AI into a software development workflow using a simple and functional prototype.

---

## Problem Statement

Developers often need quick feedback on their code before submitting it for review.

Manual code reviews require time and availability of senior developers.

This project provides an AI-assisted review process that can instantly generate actionable feedback on small code snippets.

---

## Goals

### Primary Goals

* Accept source code as input
* Analyze code using Gemini API
* Generate structured review feedback
* Display results in a clean interface

### Success Criteria

* User can submit code successfully
* AI generates a review within a few seconds
* Feedback is easy to read and understand

---

## Target Users

* Students learning programming
* Beginner developers
* Developers seeking quick code feedback

---

## User Flow

1. User opens the application.
2. User selects programming language.
3. User pastes code into the editor.
4. User clicks "Review Code".
5. Frontend sends request to Spring Boot backend.
6. Backend sends prompt to Gemini API.
7. Gemini analyzes code and returns feedback.
8. Backend returns structured review.
9. Frontend displays the review.

---

## Core Features (MVP)

### Feature 1: Language Selection

User can select:

* Java
* JavaScript
* Python
* C++

Purpose:
Provide context to the AI model.

---

### Feature 2: Code Submission

User can paste source code into a text area.

Requirements:

* Minimum 1 character
* Maximum reasonable limit (e.g., 5000 characters)

---

### Feature 3: AI Code Review

System sends code and language information to Gemini.

The AI should return feedback in the following structure:

#### Bugs & Issues

Potential errors or risky code patterns.

#### Performance Suggestions

Opportunities to improve efficiency.

#### Best Practices

Coding standards and maintainability improvements.

#### Improved Code

A revised version of the submitted code.

---

### Feature 4: Review Display

Display all review sections clearly.

Sections:

* Bugs & Issues
* Performance Suggestions
* Best Practices
* Improved Code

---

## Non-Goals

The following features are intentionally excluded:

* User authentication
* Database storage
* Review history
* Team collaboration
* File upload
* GitHub integration
* Real-time editing
* Multiple AI providers
* Syntax highlighting
* Export to PDF

These features add complexity and are outside the MVP scope.

---

## Functional Requirements

### Frontend

* Language dropdown
* Code textarea
* Submit button
* Review results section
* Loading indicator while review is generated

### Backend

* REST API endpoint

POST /api/review

Request:

```json
{
  "language": "Java",
  "code": "..."
}
```

Response:

```json
{
  "bugs": "...",
  "performance": "...",
  "bestPractices": "...",
  "improvedCode": "..."
}
```

* Integrate with Gemini API
* Handle API errors gracefully

---

## Technical Stack

### Frontend

* React
* Vite
* CSS
* Fetch API

### Backend

* Java 21
* Spring Boot 4.0.6
* Spring Web
* WebClient (Spring Reactive Web)
* Lombok
* Maven

### AI

* Gemini API (gemini-2.5-flash)

---

## Architecture

```
React Frontend (Vite)
        ↓
Spring Boot REST API
(io.github.atsin6.codereviewer)
        ↓
    Gemini API
        ↓
  Review Response
        ↓
 Frontend Display
```

---

## Limitations

* AI feedback may not always be correct.
* Large codebases are not supported.
* No execution or compilation of code.
* Reviews are generated solely from prompt-based analysis.

---

## Future Enhancements

* Authentication
* Review history
* GitHub integration
* Multiple AI models
* Syntax-highlighted editor
* File upload support
* Team collaboration features

---

## Timeline

### Day 1

* Setup Spring Boot project
* Setup React + Vite frontend
* Integrate Gemini API
* Create review endpoint
* Basic frontend UI

### Day 2

* Improve prompts
* Frontend-backend integration
* Handle errors
* Test application
* Prepare architecture documentation
