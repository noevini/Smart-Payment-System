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
- Java 17
- Spring Boot 3
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

**Deployment:**
- Railway (Backend + Frontend + Database)

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

## Live Demo

Frontend: https://fearless-creation-production-1a99.up.railway.app

Backend API: https://smart-payment-system-production.up.railway.app

API Documentation (Swagger): 
https://smart-payment-system-production.up.railway.app/swagger-ui.html

## Project Structure
Smart-Payment-System/
├── payment-system-api/          # Spring Boot backend
│   ├── src/main/java/com/smartpaymentsystem/
│   │   ├── api/                 # Controllers, DTOs, Exception handlers
│   │   ├── config/              # Security, JWT, CORS configuration
│   │   ├── domain/              # Domain entities and enums
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── security/            # JWT filter, Business access service
│   │   └── service/             # Business logic services
│   ├── src/main/resources/
│   │   └── db/migration/        # Flyway migration scripts
│   ├── src/test/                # JUnit 5 unit tests (92 tests)
│   └── pom.xml
│
└── smartpayUI/                  # React frontend
├── src/
│   ├── api/                 # API client functions
│   ├── components/          # Reusable UI components
│   ├── hooks/               # Custom React hooks
│   ├── pages/               # Page components
│   └── auth/                # Authentication utilities
└── package.json

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven

### Backend Setup

1. Clone the repository:
```bash
git clone https://github.com/noevini/Smart-Payment-System.git
cd Smart-Payment-System/payment-system-api
```

2. Configure environment variables:
DATABASE_URL=jdbc:postgresql://localhost:5432/smartpay
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_secret_key
JWT_EXPIRATION_MINUTES=60
SPRING_AI_OPENAI_API_KEY=your_openai_key
CORS_ALLOWED_ORIGINS=http://localhost:5173

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Access Swagger UI:
http://localhost:8080/swagger-ui.html

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd Smart-Payment-System/smartpayUI
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment variables:
VITE_API_BASE_URL=http://localhost:8080

4. Run the development server:
```bash
npm run dev
```

5. Access the application:
http://localhost:5173

## Default Credentials (Demo)

To test the live demo, register a new OWNER account at:
https://fearless-creation-production-1a99.up.railway.app/register

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
