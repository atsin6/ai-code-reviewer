# Implementation Plan: Phase 1.2 - Review History & Database Persistence

## Goal Description
Add database persistence for code reviews so that users can view their past submissions and the AI's feedback. This phase will introduce the `Review` entity, tie it securely to the authenticated `User`, and build a frontend history view.

## Proposed Changes

---

### Database & Entities

#### [NEW] Review.java
- Create JPA entity for reviews.
- Fields: `id`, `ManyToOne User user`, `language`, `code` (`@Lob`), `bugs` (`@Lob`), `performance` (`@Lob`), `bestPractices` (`@Lob`), `improvedCode` (`@Lob`), `createdAt`.

#### [MODIFY] User.java
- Add `@OneToMany(mappedBy = "user")` for the list of reviews (optional, primarily for cascading deletes if needed).

---

### Data Access Layer

#### [NEW] ReviewRepository.java
- Extends `JpaRepository<Review, Long>`.
- Add `List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);`
- Add `Optional<Review> findByIdAndUserId(Long id, Long userId);` to enforce BOLA at the query level.

#### [NEW] ReviewHistoryResponse.java
- DTO for returning a summary of past reviews (id, language, snippet of code, timestamp).

---

### Service Layer

#### [MODIFY] CodeReviewService.java
- Update the `reviewCode` method to accept the `User` object (or `userId`).
- After fetching the Gemini response, map it to a new `Review` entity and save it using `ReviewRepository`.
- Create new methods: `List<ReviewHistoryResponse> getUserReviews(Long userId)` and `ReviewResponse getReviewDetails(Long reviewId, Long userId)`.

---

### Controller Layer

#### [MODIFY] ReviewController.java
- Extract the authenticated user's ID from the `JwtAuthFilter` via `SecurityContextHolder`.
- Update `POST /api/review` to pass the user ID to the service.
- Add `GET /api/reviews` to fetch user's review history.
- Add `GET /api/reviews/{id}` to fetch specific review details (BOLA secured).

---

### Frontend Integration

#### [MODIFY] package.json
- Add `react-router-dom` to handle multi-page navigation.

#### [NEW] History.jsx
- Fetch and display a list of the user's past reviews in a card/table format.

#### [NEW] ReviewDetails.jsx
- Display the full AI feedback for a specific historical review.

#### [MODIFY] App.jsx
- Wrap application in `BrowserRouter`.
- Setup routes: `/` (Home/New Review), `/history`, `/review/:id`, and conditionally redirect based on auth state.
- Add a navigation bar for authenticated users to switch between "New Review" and "History".

#### [MODIFY] api.js
- Add `getReviewHistory()` and `getReviewDetails(id)` methods with Bearer token inclusion.

---

## Verification Plan

### Automated Tests
- Unit tests for `ReviewRepository` checking custom queries.
- Unit tests for `CodeReviewService` mocking both Gemini API and `ReviewRepository`.
- Integration tests for `GET /api/reviews` and `GET /api/reviews/{id}` asserting `200 OK` for the owner and `403/404` for unauthorized cross-user access (BOLA test).

### Manual Verification
- Log in on frontend, submit a code review.
- Navigate to "History" and see the review appear.
- Click the review and view the saved details.
