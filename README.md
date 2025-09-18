# QuizApp

A modern, full-stack quiz application with a native Android client and a FastAPI backend. The app supports teacher and student roles, secure authentication, profile management, and a beautiful, responsive UI built with Jetpack Compose.

> You can enhance this README with screenshots and GIFs. Add them to a `docs/` folder and link them under the Screenshots section.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup (FastAPI)](#backend-setup-fastapi)
  - [Android App Setup](#android-app-setup)
- [Environment Variables](#environment-variables)
- [API Overview](#api-overview)
- [Screenshots](#screenshots)
- [Development Notes](#development-notes)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## Overview
QuizApp enables teachers to create and manage quizzes while students can join, solve, and review results. The Android app offers a polished UI/UX with animated, modern components, offline-friendly behavior, and safe navigation patterns. The backend provides robust, token-based APIs for authentication and quiz workflows.

## Features
- Teacher & Student roles
- Secure Auth (token-based)
- Profile view/edit for both roles
- Create, list, join quizzes
- Solve with countdown timer & auto-submit when time runs out
- Result summary & detailed per-question review
- Student history of solved quizzes (newest-first sorting)
- Beautiful UI with gradients, cards, and icons (Jetpack Compose)

## Architecture
- Clean separation between client and server
- Android app uses MVVM with repositories
- Backend uses FastAPI with SQLAlchemy ORM, Pydantic schemas
- Stateless APIs secured with Bearer tokens

```mermaid
flowchart LR
  A[Android App (Jetpack Compose)] -->|HTTPS| B[FastAPI Backend]
  B --> C[(DB: SQLAlchemy)]
  A <-.-> D[Local Storage (SharedPreferences)]
```

## Tech Stack
- Android: Kotlin, Jetpack Compose, Material 3, Navigation, ViewModel, Coroutines, Retrofit (or similar), SharedPreferences
- Backend: Python, FastAPI, SQLAlchemy, Pydantic, Uvicorn
- Build/Tools: Gradle, pip/venv, Git

## Project Structure
```
quizapp/
├─ android/          # Android application (Jetpack Compose)
│  ├─ app/
│  └─ ...
├─ backend/          # FastAPI application
│  ├─ app/
│  │  ├─ main.py
│  │  ├─ api/
│  │  ├─ models/
│  │  ├─ schemas/
│  │  └─ services/
│  └─ requirements.txt
└─ README.md         # You are here
```

## Getting Started

### Prerequisites
- Android Studio (Giraffe/Flamingo or newer)
- JDK 17 (recommended for latest Android Gradle Plugin)
- Python 3.10+
- Git

### Backend Setup (FastAPI)
1. Create and activate a Python virtual environment:
   ```bash
   cd backend
   python -m venv .venv
   # Windows
   .venv\Scripts\activate
   # macOS/Linux
   source .venv/bin/activate
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Configure environment variables (see [Environment Variables](#environment-variables)).
4. Run the server (auto-reload):
   ```bash
   uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
   ```
5. Open API docs:
   - Swagger UI: http://localhost:8000/docs
   - ReDoc: http://localhost:8000/redoc

### Android App Setup
1. Open `android/` in Android Studio.
2. Sync Gradle and build the project.
3. Configure `BASE_URL` for your backend (e.g., in a constants file or build config). If using emulator:
   - Android Emulator to host machine: `http://10.0.2.2:8000`
4. Run the app on a device or emulator.

## Environment Variables
Backend (examples; adapt to your configuration):
- `DATABASE_URL`: SQLAlchemy connection string (e.g., `sqlite:///./quiz.db` or PostgreSQL URL)
- `SECRET_KEY`: JWT secret key
- `ACCESS_TOKEN_EXPIRE_MINUTES`: Token TTL (e.g., `60`)
- `CORS_ORIGINS`: Allowed origins (e.g., `http://localhost:3000,http://10.0.2.2:8000`)

Android:
- `BASE_URL`: Backend base URL. Example for emulator: `http://10.0.2.2:8000`
- Store tokens in `SharedPreferences` (already used as `quiz_app_prefs` with `access_token`).

## API Overview
Key endpoints (representative; see Swagger for full list):
- Auth
  - `POST /auth/login`
  - `POST /auth/register`
- Profile
  - `GET /users/me`
  - `PUT /users/me`
- Quizzes
  - `GET /quizzes` (list)
  - `POST /quizzes` (teacher)
  - `GET /quizzes/{id}` (details with questions)
  - `POST /quizzes/{id}/submit` (submit answers)
- Student
  - `GET /students/me/solved-quizzes` (history)
  - `GET /students/me/results/{quiz_id}` (detailed result)

All protected endpoints require `Authorization: Bearer <token>` header.

## Screenshots
Add your images to `docs/` and reference them here.

- Student Home
  - ![Student Home](docs/student_home.png)
- Join Quiz
  - ![Join Quiz](docs/join_quiz.png)
- Solve Quiz / Timer
  - ![Solve Quiz](docs/solve_quiz.png)
- Results
  - ![Results](docs/results.png)

## Development Notes
- UI uses Compose Material 3 and gradient backgrounds for modern look.
- Navigation is handled with `NavController` and back behavior is provided in screens (with default system back).
- Token is stored in `SharedPreferences` under `quiz_app_prefs` as `access_token`.
- Some Compose APIs are marked experimental; we opt-in where needed.
- The Android minSdk is assumed to be 24+; time parsing avoids `java.time` to ensure compatibility.

## Troubleshooting
- Android cannot reach backend:
  - Use `10.0.2.2` for emulator to access your host machine.
  - Check CORS and server bind address.
- Token missing or expired:
  - Re-login; ensure token is written to `SharedPreferences` correctly.
- Build fails on experimental Material APIs:
  - Ensure `@OptIn(ExperimentalMaterial3Api::class)` is present where needed.
- Date parsing issues in solved quiz list:
  - The app supports ISO strings (with/without timezone) and epoch seconds/millis. If your backend returns a different format, update the patterns in `StudentSolvedQuizListScreen.kt`.

## License
This project is licensed under the MIT License. See `LICENSE` for details.
