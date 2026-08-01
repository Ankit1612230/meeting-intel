# Meeting Intelligence Platform

An AI-powered B2B Meeting Intelligence REST API that analyzes meeting transcripts and automatically extracts action items, decisions, and summaries using Groq LLM.

---

## 🚀 What it does

Most teams struggle to track what was decided in meetings and who is responsible for what. This platform solves that by:

* Submit a meeting transcript after your meeting.
* The AI reads it and extracts:

    * Summary
    * Key decisions
    * Action items with owners and due dates
* The system automatically sends email reminders to task owners before their deadlines.

---

# 🛠 Tech Stack

| Layer             | Technology                                       |
| ----------------- | ------------------------------------------------ |
| Backend Framework | Spring Boot 3.4.4                                |
| Language          | Java 21                                          |
| AI Integration    | Groq LLM (llama-3.3-70b-versatile) via WebClient |
| Security          | Spring Security + JWT                            |
| Database          | PostgreSQL 16                                    |
| ORM               | Spring Data JPA + Hibernate                      |
| Containerization  | Docker + Docker Compose                          |
| Email             | Spring Mail + Gmail SMTP                         |
| Scheduler         | Spring Scheduler                                 |
| Logging           | Spring AOP                                       |
| API Docs          | Swagger / OpenAPI 3.0                            |
| Build Tool        | Maven                                            |

---

# 📂 Project Structure

```text
src/main/java/com/meetingintel/meeting_intel/
├── MeetingIntelApplication.java
├── controller/
│   ├── AuthController.java
│   ├── MeetingController.java
│   └── AnalysisController.java
├── service/
│   ├── AuthService.java
│   ├── MeetingService.java
│   ├── AnalysisService.java
│   ├── GroqAiService.java
│   ├── EmailService.java
│   ├── ReminderSchedulerService.java
│   └── CustomUserDetailsService.java
├── entity/
│   ├── User.java
│   ├── Meeting.java
│   ├── MeetingInsight.java
│   ├── ActionItem.java
│   ├── MeetingStatus.java
│   └── ActionItemStatus.java
├── repository/
│   ├── UserRepository.java
│   ├── MeetingRepository.java
│   ├── MeetingInsightRepository.java
│   └── ActionItemRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
├── dto/
│   ├── RegisterRequest.java
│   ├── MeetingRequest.java
│   └── MeetingResponse.java
├── aspect/
│   └── LoggingAspect.java
└── config/
    └── SwaggerConfig.java
```

---

# 📡 API Endpoints

## Authentication

| Method | Endpoint             | Description             |
| ------ | -------------------- | ----------------------- |
| POST   | `/api/auth/register` | Register new user       |
| POST   | `/api/auth/login`    | Login and get JWT token |

## Meetings

| Method | Endpoint                        | Description                |
| ------ | ------------------------------- | -------------------------- |
| POST   | `/api/meetings`                 | Create a new meeting       |
| GET    | `/api/meetings`                 | Get all meetings           |
| GET    | `/api/meetings/{id}`            | Get meeting by ID          |
| DELETE | `/api/meetings/{id}`            | Delete a meeting           |
| GET    | `/api/meetings/search?keyword=` | Search meetings by keyword |
| GET    | `/api/meetings/dashboard`       | Get dashboard metrics      |

## AI Analysis

| Method | Endpoint                          | Description                |
| ------ | --------------------------------- | -------------------------- |
| POST   | `/api/meetings/{id}/analyze`      | Analyze transcript with AI |
| GET    | `/api/meetings/{id}/insights`     | Get AI-generated insights  |
| GET    | `/api/meetings/{id}/action-items` | Get extracted action items |

## Action Items

| Method | Endpoint                                                   | Description              |
| ------ | ---------------------------------------------------------- | ------------------------ |
| PATCH  | `/api/meetings/{meetingId}/action-items/{itemId}/complete` | Mark action item as done |
| GET    | `/api/meetings/action-items/my-pending`                    | Get my pending tasks     |

---

# 🗄 Database Schema

## users

```text
id (PK)
full_name
email (unique)
password (BCrypt hashed)
created_at
```

## meetings

```text
id (PK)
title
meeting_date
transcript (TEXT)
status (PENDING / ANALYZED)
created_by (FK → users)
created_at
```

## meeting_participants

```text
meeting_id (FK → meetings)
email
```

## meeting_insights

```text
id (PK)
meeting_id (FK → meetings)
summary
decisions
raw_ai_response
analyzed_at
```

## action_items

```text
id (PK)
meeting_id (FK → meetings)
task
owner_email
due_date
status (PENDING / DONE)
completed_at
created_at
```

---

# ▶ How to Run Locally

## Prerequisites

* Java 21
* Docker Desktop
* Maven (or IntelliJ built-in)
* Groq API Key (free at console.groq.com)
* Gmail account with App Password

---

## Step 1 — Clone the Repository

```bash
git clone https://github.com/Ankit1612230/meeting-intel.git
cd meeting-intel
```

---

## Step 2 — Create `application.properties`

Create:

```text
src/main/resources/application.properties
```

```properties
spring.application.name=meeting-intel

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/meetingdb
spring.datasource.username=admin
spring.datasource.password=admin123
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# Docker compose
spring.docker.compose.enabled=false

# Groq AI
groq.api.key=YOUR_GROQ_API_KEY
groq.api.url=https://api.groq.com/openai/v1/chat/completions
groq.model=llama-3.3-70b-versatile

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## Step 3 — Start PostgreSQL

```bash
docker-compose up -d
```

---

## Step 4 — Run the Application

Open `MeetingIntelApplication.java` in IntelliJ and click the green **Run** button.

---

## Step 5 — Open Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 🧪 How to Test

## 1. Register

```json
POST /api/auth/register

{
  "fullName": "Your Name",
  "email": "you@gmail.com",
  "password": "yourpassword"
}
```

---

## 2. Login and Copy JWT Token

```json
POST /api/auth/login

{
  "email": "you@gmail.com",
  "password": "yourpassword"
}
```

---

## 3. Authorize in Swagger

Click **Authorize** → Paste the JWT Token → Click **Authorize**

---

## 4. Create a Meeting

```json
POST /api/meetings

{
  "title": "Q4 Planning",
  "meetingDate": "2026-04-20T10:00:00",
  "participants": [
    "alice@gmail.com",
    "bob@gmail.com"
  ],
  "transcript": "Alice will prepare the budget report by April 28th. Bob will send the client proposal by April 25th. Team decided to launch the product in Q3."
}
```

---

## 5. Analyze the Transcript

```http
POST /api/meetings/{id}/analyze
```

---

## 6. View Extracted Action Items

```http
GET /api/meetings/{id}/action-items
```

---

# ✨ Key Features

* 🤖 AI Transcript Analysis — Sends transcript to Groq LLM and extracts structured data automatically.
* 🔐 JWT Security — Stateless authentication for every request.
* 📧 Email Reminders — Daily scheduler at **9:00 AM** sends reminders for tasks due the next day.
* 📊 Dashboard — Real-time metrics for meetings and action items.
* 🔍 Search — Search meeting titles and transcripts by keyword.
* 📝 AOP Logging — Logs service method execution time automatically.
* 📖 Swagger UI — Interactive API documentation with JWT support.

---

# 👨‍💻 Author

**Ankit**
