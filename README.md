# DriveEasy Rentals 🚗

> A full-stack car rental management platform built with Spring Boot 3, Thymeleaf, Spring Security, JWT, and MySQL.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture Summary](#architecture-summary)
- [Tech Stack](#tech-stack)
- [Phase History](#phase-history)
    - [Phase 1 — Console Application](#phase-1--console-application)
    - [Phase 2 — Web Application (MVC + Thymeleaf)](#phase-2--web-application-mvc--thymeleaf)
    - [Phase 3 — REST API + JWT (Current)](#phase-3--rest-api--jwt-current)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Fare Calculation Formula](#fare-calculation-formula)
- [Security Model](#security-model)
- [REST API Reference](#rest-api-reference)
- [Running the Application](#running-the-application)
- [Default Credentials](#default-credentials)
- [Known Bugs Fixed](#known-bugs-fixed)
- [Phase 4 — End-User Application (Planned)](#phase-4--end-user-application-planned)
- [Phase 5 — Future Vision](#phase-5--future-vision)

---

## Project Overview

DriveEasy Rentals is a car rental platform that has evolved through three distinct phases — from a console-based JDBC app to a production-grade Spring Boot web application with REST APIs and JWT authentication.

The system supports two internal roles (**Admin** and **Staff**) managing a fleet of cars, customers, and reservations — and is now being extended in Phase 3/4 to serve **end users (customers)** directly through a public-facing REST API.

---

## Architecture Summary

```
┌─────────────────────────────────────────────────────────┐
│                  DriveEasy Rentals                      │
│                                                         │
│  ┌──────────────┐      ┌──────────────────────────────┐ │
│  │  Thymeleaf   │      │   REST API (/api/v1/*)        │ │
│  │  MVC UI      │      │   JWT-secured, stateless      │ │
│  │  (Staff/Admin│      │   Swagger UI at /swagger-ui  │ │
│  │   session)   │      │                              │ │
│  └──────┬───────┘      └──────────────┬───────────────┘ │
│         │                             │                  │
│  ┌──────▼─────────────────────────────▼───────────────┐ │
│  │              Service Layer                          │ │
│  │  CarService | CustomerService | ReservationService  │ │
│  │  FareCalculator                                     │ │
│  └──────────────────────┬──────────────────────────────┘ │
│                         │                                │
│  ┌──────────────────────▼──────────────────────────────┐ │
│  │     Spring Data JPA Repositories + MySQL            │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Language | Java 17 |
| Web (MVC) | Spring MVC + Thymeleaf |
| REST API | Spring MVC REST Controllers |
| Security | Spring Security 6 + JWT (jjwt 0.12.5) |
| Database | MySQL 8 + Spring Data JPA (Hibernate) |
| API Docs | SpringDoc OpenAPI / Swagger UI 2.5 |
| Build | Maven |
| Frontend (MVC) | HTML5 + CSS3 + Font Awesome 6 |

---

## Phase History

### Phase 1 — Console Application

**Status:** Complete (legacy code removed, ConsoleApp retained under `--spring.profiles.active=console`)

What was built:
- Plain Java console UI (`ConsoleApp.java`)
- Raw JDBC with `DbConnectionManager` for MySQL
- Manual DAO layer: `CarDaoImpl`, `CustomerDaoImpl`, `ReservationDaoImpl`
- Basic fare calculation as a simple formula in the service
- Manual ID assignment for all entities

Key bugs fixed when migrating to Phase 2:
- All DAO classes removed (replaced by Spring Data JPA repositories)
- Manual ID constructors removed from `Car`, `Customer`, `Reservation`
- `ConsoleApp` rewired as a Spring `@Component + CommandLineRunner` so services are properly injected

---

### Phase 2 — Web Application (MVC + Thymeleaf)

**Status:** Complete and stable

What was built:
- Spring Boot web application with Thymeleaf templates
- Full CRUD for Cars, Customers, and Reservations via browser UI
- Session-based authentication with Spring Security form login
- Two roles: `ADMIN` (full access) and `STAFF` (booking operations)
- Two-step booking flow: date availability check → fare preview → confirm
- Itemised fare breakdown (`FareBreakdown` DTO) with category surcharge
- Dashboard with live stats (fleet count, revenue, active bookings)
- `GlobalExceptionHandler` for MVC error pages

Pages:
- `/` — Dashboard
- `/login` — Login page
- `/admin/cars` — Fleet management (Admin only)
- `/staff/customers` — Customer directory
- `/staff/reservations` — Reservation ledger + booking flow

---

### Phase 3 — REST API + JWT (Current)

**Status:** In progress

What was added:
- Dual Spring Security filter chains: Chain 1 = stateless JWT for `/api/**`, Chain 2 = session-based MVC
- `JwtService` — token generation, validation, and claims extraction (HMAC-SHA256)
- `JwtAuthFilter` — `OncePerRequestFilter` that reads `Authorization: Bearer <token>`
- `JwtProperties` — externalized config (`jwt.secret`, `jwt.expiration-ms`)
- REST controllers under `/api/v1/`:
    - `AuthApiController` — `/api/v1/auth/login`, `/api/v1/auth/register`
    - `CarApiController` — fleet read/write endpoints
    - `CustomerApiController` — customer management
    - `ReservationApiController` — booking lifecycle + revenue
- `ApiExceptionHandler` (`@RestControllerAdvice`) — structured JSON error responses
- Response DTOs: `AuthResponse`, `CarResponse`, `CustomerResponse`, `ReservationResponse`, `FarePreviewResponse`
- Request DTOs with Bean Validation: `LoginRequest`, `RegisterRequest`, `BookingRequest`
- Swagger UI at `/swagger-ui.html` with "Authorize" button for JWT

---

## Project Structure

```
src/main/java/com/driveeasy/
├── DriveEasyApplication.java
├── api/v1/
│   ├── AuthApiController.java       # POST /api/v1/auth/login, /register
│   ├── CarApiController.java        # GET/POST/PUT/PATCH /api/v1/.../cars
│   ├── CustomerApiController.java   # GET/POST /api/v1/staff/customers
│   └── ReservationApiController.java
├── config/
│   └── OpenApiConfig.java           # Swagger/OpenAPI configuration
├── controller/                      # Thymeleaf MVC controllers
│   ├── AuthController.java
│   ├── CarController.java
│   ├── CustomerController.java
│   ├── DashboardController.java
│   └── ReservationController.java
├── dto/
│   ├── request/  (LoginRequest, RegisterRequest, BookingRequest)
│   └── response/ (AuthResponse, CarResponse, CustomerResponse,
│                  ReservationResponse, FarePreviewResponse)
├── exception/
│   ├── ApiExceptionHandler.java     # REST JSON errors
│   ├── GlobalExceptionHandler.java  # MVC HTML errors
│   ├── BookingConflictException.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
├── model/
│   ├── Car.java
│   ├── Customer.java
│   ├── Reservation.java
│   ├── User.java
│   ├── dto/FareBreakdown.java
│   └── enums/ (CarCategory, ReservationStatus, Role)
├── repository/
│   ├── CarRepository.java
│   ├── CustomerRepository.java
│   ├── ReservationRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthFilter.java
│   ├── JwtProperties.java
│   ├── JwtService.java
│   ├── SecurityConfig.java          # Dual filter chain
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── CarService.java
│   ├── CustomerService.java
│   ├── FareCalculator.java
│   └── ReservationService.java
└── ui/
    └── ConsoleApp.java              # Legacy CLI (profile: console)
```

---

## Database Schema

```sql
car          (id, model, category, base_fare, per_km_rate, per_hour_rate, under_maintenance)
customer     (id, name, email, phone, driving_license_number)
reservation  (id, car_id, customer_id, start_date, end_date,
              estimated_distance_km, estimated_duration_hours,
              base_fare_charged, distance_fare, duration_fare,
              category_surcharge, total_fare, status,
              created_at, cancelled_at, cancellation_reason)
users        (id, username, password, role, full_name, enabled)
```

> Schema is auto-created via `schema.sql` on startup (`spring.sql.init.mode=always`).
> Seed data is applied from `data.sql` using `WHERE NOT EXISTS` guards.

---

## Fare Calculation Formula

```
variableFare      = (distanceKm × perKmRate) + (durationHours × perHourRate)
categorySurcharge = variableFare × (categoryMultiplier − 1.0)
totalFare         = baseFare + variableFare + categorySurcharge
```

| Category | Multiplier | Surcharge on variable fare |
|---|---|---|
| ECONOMY | 1.0 | 0% |
| SEDAN | 1.2 | 20% |
| SUV | 1.5 | 50% |
| LUXURY | 2.0 | 100% |

The base fare is a flat booking fee — **not** multiplied by the category. Only the usage-based (variable) portion attracts the surcharge.

---

## Security Model

Two independent Spring Security filter chains:

**Chain 1 — API (`/api/**`)**
- Stateless, no HttpSession
- CSRF disabled
- JWT validation via `JwtAuthFilter`
- 401/403 return JSON, never redirect
- Public: `/api/v1/auth/**`, `/swagger-ui/**`, `/api-docs/**`
- Admin only: `/api/v1/admin/**`
- Staff + Admin: `/api/v1/staff/**`

**Chain 2 — MVC (everything else)**
- Session-based
- Form login at `/login`, logout at `/logout`
- Admin only: `/admin/**`
- Staff + Admin: `/staff/**`, `/`
- Redirects to `/access-denied` on 403

---

## REST API Reference

### Authentication

```
POST /api/v1/auth/login
Body: { "username": "admin", "password": "admin123" }
Response: { "token": "...", "username": "admin", "role": "ADMIN", "expiresInMs": 86400000 }

POST /api/v1/auth/register
Body: { "username": "...", "password": "...", "fullName": "...", "role": "STAFF" }
```

All subsequent requests require:
```
Authorization: Bearer <token>
```

### Cars

```
GET    /api/v1/staff/cars                          # List all cars
GET    /api/v1/staff/cars/{id}                     # Get by ID
GET    /api/v1/staff/cars/available?startDate=&endDate=   # Available cars
POST   /api/v1/admin/cars                          # Add car (Admin)
PUT    /api/v1/admin/cars/{id}/pricing             # Update pricing (Admin)
PATCH  /api/v1/admin/cars/{id}/maintenance         # Toggle maintenance (Admin)
```

### Customers

```
GET    /api/v1/staff/customers                     # List all customers
GET    /api/v1/staff/customers/{id}                # Get by ID
POST   /api/v1/staff/customers                     # Register customer
```

### Reservations

```
GET    /api/v1/staff/reservations                  # All reservations
GET    /api/v1/staff/reservations/{id}             # Get by ID
GET    /api/v1/staff/reservations/fare-preview     # Preview fare
GET    /api/v1/staff/reservations/customer/{id}    # By customer
GET    /api/v1/staff/reservations/revenue          # Total revenue
POST   /api/v1/staff/reservations                  # Book car
PATCH  /api/v1/staff/reservations/{id}/cancel      # Cancel
PATCH  /api/v1/staff/reservations/{id}/complete    # Complete
```

Full interactive docs: **http://localhost:8080/swagger-ui.html**

---

## Running the Application

### Prerequisites

- Java 17+
- MySQL 8+ running locally
- Maven 3.8+

### Steps

```bash
# 1. Create the database (schema.sql handles table creation automatically)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS driveeasy;"

# 2. Update credentials in src/main/resources/application.properties
spring.datasource.username=root
spring.datasource.password=your_password

# 3. Run
mvn spring-boot:run

# Or run the console mode (legacy CLI)
mvn spring-boot:run -Dspring-boot.run.profiles=console
```

Application starts at **http://localhost:8080**

---

## Default Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Staff | `staff` | `staff123` |

Passwords are BCrypt-hashed (strength 12) in `data.sql`.

> **Important:** Change the JWT secret in `application.properties` before any production deployment.
> Current secret: `driveeasy-super-secret-key-phase3-minimum-256-bits-long-replace-in-prod`

---

## Known Bugs Fixed

| # | Bug | Fix Applied |
|---|---|---|
| 1 | `@RequestParam String model_` didn't bind HTML form field `model` | Changed to `@RequestParam("model") String carModel` |
| 2 | `findByCustomerIdOrderByCreatedAtDesc` threw `PropertyReferenceException` | Renamed to `findByCustomer_IdOrderByCreatedAtDesc` (underscore notation for joins) |
| 3 | `getTotalRevenue()` returned primitive `double` causing NPE on empty tables | Changed return type to boxed `Double`, added null-guard in callers |
| 4 | Cancellation guard used `!startDate.isAfter(now)` blocking same-day cancellations | Corrected to `startDate.isBefore(now)` |
| 5 | `ConsoleApp` instantiated services with `new CarService()` — no repositories injected | Converted to Spring `@Component + CommandLineRunner` |
| 6 | Phase-1 constructors with manual IDs on Car/Customer/Reservation still present | Removed; only ID-less constructors remain |

---

## Phase 4 — End-User Application (Planned)

Phase 4 will extend the platform with a **customer-facing self-service layer**. The internal staff/admin system remains unchanged.

### What to Build

**Customer Self-Registration & Authentication**
- `POST /api/v1/public/register` — customer self-signup with email verification
- `POST /api/v1/public/login` — returns JWT for customer role
- New `CUSTOMER` role in `Role` enum and security config
- Customer profile endpoint: `GET /api/v1/customer/me`

**Car Browsing (Public, No Auth)**
- `GET /api/v1/public/cars` — list available cars with optional filters (category, price range, dates)
- `GET /api/v1/public/cars/{id}` — car detail with pricing
- `GET /api/v1/public/cars/available?startDate=&endDate=` — availability check

**Self-Service Booking**
- `POST /api/v1/customer/reservations` — book a car (authenticated customer only)
- `GET /api/v1/customer/reservations` — customer's own booking history
- `GET /api/v1/customer/reservations/{id}` — booking detail + fare breakdown
- `PATCH /api/v1/customer/reservations/{id}/cancel` — self-cancel with reason
- `GET /api/v1/customer/reservations/fare-preview` — fare estimate before booking

**Profile Management**
- `GET /api/v1/customer/profile` — view profile
- `PUT /api/v1/customer/profile` — update name, phone, driving license
- `PUT /api/v1/customer/profile/password` — change password

**New Security Chain**
- Chain 1 remains: `/api/v1/admin/**` and `/api/v1/staff/**` (JWT, ADMIN/STAFF roles)
- Add public endpoints: `/api/v1/public/**` (no auth)
- Add customer endpoints: `/api/v1/customer/**` (JWT, CUSTOMER role)

**Data Model Changes**
- `Role` enum: add `CUSTOMER`
- `Customer` entity: link to `User` account (optional FK `user_id`)
- Or: create separate `CustomerAccount` entity
- Add `email_verified` flag and `verification_token` to users

**New DTOs**
- `CustomerRegisterRequest` (name, email, phone, password, drivingLicenseNumber)
- `CustomerProfileResponse`
- `PublicCarResponse` (hide internal fields like maintenance flag)
- `CustomerBookingRequest` (carId, startDate, endDate, estimatedDistanceKm, estimatedDurationHours)

**Frontend (optional — React or Thymeleaf)**
- Customer-facing landing page with car search
- Login / Register page
- My Bookings page
- Booking flow with fare preview

---

## Phase 5 — Future Vision

Features to consider after Phase 4 is complete:

**Payments Integration**
- Razorpay / Stripe payment gateway for online booking payments
- Payment status tracking on reservations (`PENDING_PAYMENT`, `PAID`, `REFUNDED`)
- Invoice PDF generation and email delivery

**Notifications**
- Email notifications on booking confirmation, cancellation, and reminders (Spring Mail / SendGrid)
- SMS via Twilio for booking alerts

**Advanced Fleet Management**
- Car images (file upload + S3/local storage)
- Odometer tracking and service history
- Real-time GPS tracking integration
- Damage reports linked to reservations

**Reviews & Ratings**
- Customers rate cars after completed rentals
- Star ratings displayed on public car listings

**Promotions & Coupons**
- Discount codes at checkout
- Loyalty points system

**Reporting & Analytics**
- Admin dashboard with revenue charts (monthly, by category, by car)
- Utilization rate per vehicle
- Export reports to Excel / PDF

**Multi-tenant / Franchise Support**
- Multiple rental company branches
- Branch-specific fleet and staff assignment

**Mobile Application**
- React Native / Flutter app consuming the existing REST API
- Push notifications for booking updates

**DevOps**
- Dockerize the application (Dockerfile + docker-compose with MySQL)
- CI/CD pipeline (GitHub Actions)
- Environment-specific configs (dev / staging / prod profiles)
- Centralized logging (ELK stack or similar)

---

## Configuration Reference

Key settings in `src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/driveeasy` | MySQL connection |
| `spring.jpa.hibernate.ddl-auto` | `none` | Schema managed by `schema.sql` |
| `spring.sql.init.mode` | `always` | Runs `schema.sql` + `data.sql` on startup |
| `jwt.secret` | `driveeasy-super-secret-...` | **Replace before production** |
| `jwt.expiration-ms` | `86400000` | Token TTL: 24 hours |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Swagger UI URL |

---

*DriveEasy Rentals — Phase 3.0.0-SNAPSHOT*