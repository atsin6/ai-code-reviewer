# UI/UX Brief

# AI-Powered Code Reviewer

Version: 1.0

---

# 1. Purpose

This document defines the user interface and experience requirements for the AI-Powered Code Reviewer MVP.

---

# 2. Design Goals

* **Simplicity** — Users understand the app immediately without instructions
* **Fast Interaction** — Entire workflow in under one minute
* **Readability** — AI feedback easy to scan and understand
* **Professional Appearance** — Looks like a modern developer tool

---

# 3. Application Layout

```
+------------------------------------------------+
|           AI Code Reviewer                     |
|   Get instant AI-powered feedback on code      |
+------------------------------------------------+

  Language:  [ Java ▼ ]

  Code:
  +----------------------------------------------+
  |                                              |
  |         Paste your code here...              |
  |                                              |
  +----------------------------------------------+

              [ Review Code ]

------------------------------------------------

  Results (shown after submission):

  📋 Summary / Bugs & Issues
  ⚡ Performance Suggestions
  ✅ Best Practices
  🔁 Improved Code

------------------------------------------------
```

---

# 4. Component Structure (React)

```
src/
├── components/
│   ├── Header.jsx
│   ├── LanguageSelector.jsx
│   ├── CodeInput.jsx
│   ├── ReviewButton.jsx
│   ├── LoadingSpinner.jsx
│   ├── ErrorMessage.jsx
│   └── ReviewResult.jsx
│
├── services/
│   └── api.js
│
├── App.jsx
└── main.jsx
```

---

# 5. Component Details

## Header

Displays app title and subtitle.

```
AI Code Reviewer
Get instant AI-powered feedback on your code.
```

---

## LanguageSelector

Dropdown with options:

* Java (default)
* JavaScript
* Python
* C++

---

## CodeInput

Multi-line textarea.

Requirements:

* Placeholder: `Paste your code here...`
* Minimum height for comfortable coding
* Monospace font (JetBrains Mono or monospace)

---

## ReviewButton

Primary CTA.

States:

| State | Label | Style |
|-------|-------|-------|
| Default | Review Code | Primary color |
| Loading | Reviewing... | Disabled + muted |

---

## LoadingSpinner

Shown while waiting for Gemini response.

```
Analyzing your code...
```

---

## ReviewResult

Renders four sections after successful response:

* Bugs & Issues
* Performance Suggestions
* Best Practices
* Improved Code (monospace block)

---

## ErrorMessage

Shown on validation or API failure.

```
Unable to generate review. Please try again.
```

---

# 6. Visual Style

## Color Palette

| Role | Color |
|------|-------|
| Background | Dark (`#0f172a`) |
| Card/Surface | (`#1e293b`) |
| Primary (buttons, accents) | Purple/Blue (`#7c3aed`) |
| Text | Light (`#e2e8f0`) |
| Muted text | (`#94a3b8`) |
| Success | Green (`#10b981`) |
| Error | Red (`#ef4444`) |

## Typography

* Body: `Inter`, sans-serif
* Code blocks: `JetBrains Mono`, monospace

---

# 7. Loading Experience

When user clicks Review Code:

1. Button becomes disabled and shows "Reviewing..."
2. Spinner appears with "Analyzing your code..."
3. Results section hidden
4. On success: spinner hides, results appear
5. On error: spinner hides, error message appears

---

# 8. Error States

| Scenario | Message |
|----------|---------|
| Empty code | Please enter code before submitting. |
| API failure | Unable to generate review. Please try again. |
| Network error | Connection failed. Please check your network. |

---

# 9. Responsiveness

* **Desktop** — Centered layout, max-width container
* **Tablet** — Same structure, reduced spacing
* **Mobile** — Stacked layout, full-width button

---

# 10. MVP Success Criteria

A user should be able to:

1. Open the application
2. Select a language
3. Paste code
4. Submit for review
5. Read AI-generated feedback

**Target: Complete full workflow in under 1 minute.**
