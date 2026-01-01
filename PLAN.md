
Quiz Management System

## 1. Objective

The goal of this project is to design and implement a **small but production-ready Quiz Management System** that demonstrates:

- Clean backend design using Spring Boot
- Clear separation of responsibilities
- Thoughtful scoping and trade-offs
- A working Admin and Public flow
- Reliability over feature bloat

The system simulates a real-world internal tool where admins can create quizzes and users can attempt them.


## 2. Assumptions

- The application is **single-tenant** (no multi-organization support).
- Authentication is **simple and session-based** (no OAuth).
- The system is intended for **practice quizzes**.
- Only one correct answer is assumed for MCQ and True/False questions.
- Text-based questions are **not auto-evaluated** and are shown with correct answers if available.
- Concurrency, load balancing, and advanced scaling are out of scope for this exercise.
- Database is **local(H2)** for simplicity.

---

## 3. Scope

#### Admin Panel
- Admin can:
  - Create a quiz
  - Add multiple questions
  - Choose question types:
    - MCQ
    - True/False
    - Text
- Dynamic UI for adding/removing questions and options

#### Public Page
- Anyone can:
  - View available quizzes
  - Attempt a quiz
  - Submit answers
  - View results immediately after submission:
    - Score
    - Correct / incorrect answers
    - Correct answers per question

#### System Behavior
- Quiz attempts are **evaluated in-memory**
- Results are **not persisted**
- Clean separation between UI, service, and data layers

---

### Out of Scope (By Design)

- User attempt history
- Leaderboards
- Timers / time limits
- Question randomization
- Advanced text-answer evaluation (NLP)
- Role management UI
- Password reset / forgot password
- External authentication (OAuth, SSO)
- Full Spring Security integration

These features were consciously excluded to keep the system **simple, stable, and deliverable within time constraints**.

---

## 4. High-Level Architecture

  Vaadin 24/UI Layer -> Service Layer (Spring Boot/JAX-Rs) -> Data Layer (JPA/H2)
- **UI Layer**: Vaadin 24 for server-driven UI, handling user interactions.
- **Service Layer**: Spring Boot 3 with JAX-RS (Jersey) for RESTful services, business logic.
- **Data Layer**: JPA (Hibernate) for ORM, H2 for local database.

## 5. Tech Stack & Rationale

   Frontend - Vaadin 24 
   Backend  - Spring Boot 3
   REST - JAX-RS (Jersey)
   Database - H2 
   ORM - JPA (Hibernate)
   Build Tool - Maven 
   IDE - IntelliJ 

---

## 6. Data Model (High-Level) 

### Core Entities
- **User**
    - username
    - password (hashed)
    - role (ADMIN / USER)

- **Quiz**
    - title
    - description
    - questions

- **Question**
    - text
    - type (MCQ, TRUE_FALSE, TEXT)
    - options (if applicable)

- **Option**
    - option text
    - isCorrect

> Quiz attempts and scores are intentionally **not stored**.

---

## 7. Authentication & Authorization Approach

- No Spring Security
- Session-based authentication using `VaadinSession`
- Logged-in user stored in session
- Route protection using `BeforeEnterObserver`
- UI-level access control:
    - Admin-only pages hidden for non-admin users

### Rationale
This approach reduces complexity and avoids over-engineering while remaining suitable for a small, internal-style application.

---

## 8. Scope Changes During Implementation

- Initially considered persisting quiz attempts and scores.
- Decided **not to store attempts** to:
    - Reduce schema complexity
    - Focus on core evaluation logic
    - Better align with practice-quiz use case
- Switched to Vaadin-only authentication instead of Spring Security to simplify setup and avoid unnecessary filters.

## 9. Trade-offs Made

All trade-offs were made consciously to prioritize **stability and clarity**.

---

## 10. Testing Strategy

- Manual testing through the UI
- Validation of:
    - Quiz creation
    - Quiz attempt flow
    - Answer evaluation
- Edge cases tested:
    - Empty answers
    - Invalid login
    - Non-admin attempting to access admin page

---

## 11. Reflection (What I Would Do Next)

With more time, I would:
1. Add Spring Security with proper authentication flows
2. Persist quiz attempts and display history
3. Add admin analytics (average score, attempts per quiz)
4. Improve text-answer evaluation
5. Add unit and integration tests
6. Containerize the app (Docker)
7. Deploying

---

## 12. Conclusion

This project demonstrates a **clean, production-minded approach** to building a Quiz Management System while respecting time and scope constraints.
The focus was on **working software, clarity, and maintainability** rather than feature overload.
