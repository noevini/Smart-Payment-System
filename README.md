# Smart Payment Tracking System

Backend API and frontend for a Smart Payment Tracking System developed 
as a Final Year Project for BSc Computer Science at the 
University of Westminster.

## Overview

The Smart Payment Tracking System is a secure, multi-tenant web 
application that allows small businesses to manage payments, customers, 
transactions, and financial analytics. The system supports multiple 
businesses under a single owner with strict data isolation between tenants.

## Tech Stack

**Backend:**
- Java 25
- Spring Boot 4.1
- Spring Security (JWT Authentication)
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway (Database migrations)
- Spring AI (OpenAI integration)
- Swagger / OpenAPI

**Frontend:**
- React
- Tailwind CSS
- Recharts

## Features

- JWT-based authentication with role-based access control (OWNER/STAFF)
- Multi-tenant architecture with strict business-level data isolation
- Payment lifecycle management (PENDING, PAID, OVERDUE, CANCELED)
- Customer management (CRUD)
- Transaction tracking (INCOME/EXPENSE)
- Automated overdue payment detection via scheduled job
- Internal notification system for overdue payments
- AI-powered financial analytics and insights (OpenAI)
- Payment risk predictions
- Financial reports
- Responsive React frontend with charts and dashboards

## Project Structure

    Smart-Payment-System/
    ├── backend/                     # Spring Boot backend
    │   ├── src/main/java/
    │   │   ├── api/                 # Controllers and DTOs
    │   │   ├── config/              # Security and JWT config
    │   │   ├── domain/              # Entities and enums
    │   │   ├── repository/          # JPA repositories
    │   │   ├── security/            # JWT filter and access service
    │   │   └── service/             # Business logic
    │   ├── src/main/resources/
    │   │   └── db/migration/        # Flyway scripts
    │   ├── src/test/                # JUnit tests (91 tests)
    │   └── pom.xml
    └── frontend/                    # React frontend
        ├── src/
        │   ├── api/                 # API client functions
        │   ├── components/          # UI components
        │   ├── hooks/               # Custom hooks
        │   ├── pages/               # Page components
        │   └── auth/                # Auth utilities
        └── package.json

## Getting Started

### Prerequisites

- Java 25+
- Node.js 20.19+ (22 recommended, see `frontend/.nvmrc`)
- PostgreSQL 14+
- Maven

### Backend Setup

1. Clone the repository:
```bash
git clone https://github.com/noevini/Smart-Payment-System.git
cd Smart-Payment-System/backend
```

2. Configure the application:
```bash
cp .env.example .env   # then edit the values
```
Defaults live in `src/main/resources/application.properties`. `backend/.env` is git-ignored and
loaded as an optional config import, so it must use the canonical Spring property names
(`spring.datasource.url=...`), as in `.env.example`. Real environment variables in the
`SPRING_DATASOURCE_URL` format also work (e.g. for Docker and CI). Properties defined in
`application.properties`:

| Property (`.env`) | Environment variable | Default |
|----------|--------------|---------|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/payment_tracker` |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `postgres` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | empty |
| `security.jwt.secret` | `SECURITY_JWT_SECRET` | local dev placeholder (change it) |
| `security.jwt.expiration-minutes` | `SECURITY_JWT_EXPIRATION_MINUTES` | `60` |
| `app.cors.allowed-origins` | `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:5174` |
| `app.insights.ai-enabled` | `APP_INSIGHTS_AI_ENABLED` | `false` |
| `spring.ai.openai.api-key` | `SPRING_AI_OPENAI_API_KEY` | empty |
| `spring.ai.model.chat` | `SPRING_AI_MODEL_CHAT` | `none` |

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Access Swagger UI:
http://localhost:8080/swagger

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd Smart-Payment-System/frontend
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment variables:
VITE_API_URL=http://localhost:8080

4. Run the development server:
```bash
npm run dev
```

5. Access the application:
http://localhost:5173

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /auth/register | Register new user |
| POST | /auth/login | Login and get JWT token |
| GET | /businesses | List businesses |
| POST | /businesses | Create business |
| GET | /businesses/{id}/payments | List payments |
| POST | /businesses/{id}/payments | Create payment |
| PATCH | /businesses/{id}/payments/{id} | Update payment |
| DELETE | /businesses/{id}/payments/{id} | Delete payment |
| GET | /businesses/{id}/customers | List customers |
| POST | /businesses/{id}/customers | Create customer |
| GET | /businesses/{id}/transactions | List transactions |
| POST | /businesses/{id}/transactions | Create transaction |
| GET | /businesses/{id}/analytics | Get analytics |
| GET | /businesses/{id}/insights/summary | Get AI insights |
| GET | /businesses/{id}/predictions/summary | Get predictions |
| GET | /businesses/{id}/notifications | List notifications |

## Testing

**Unit Tests (JUnit 5 + Mockito):**
```bash
cd payment-system-api
./mvnw test
```
92 tests — 100% pass rate

**API Tests:**
Postman collection available for all endpoints.
25 test cases covering authentication, payments, customers, 
transactions, multi-tenancy, and AI analytics.

## Author

Vinicius Fernandes Pereira (W1777379)
BSc Computer Science — University of Westminster
Supervisor: Dr. Ester Bonmati
