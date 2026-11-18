# Smart Hotel Management System using Service-Oriented Architecture (SOA)

An enterprise-grade, academic case study implementation of a centralized Hotel Management System demonstrating Service-Oriented Architecture (SOA) principles, dual-protocol service integration (SOAP 1.1 XML + RESTful JSON), Enterprise Architecture layers, and business value models.

---

## 1. Technology Stack

* **Backend Framework:** Java 21, Spring Boot 3.2.1
* **SOAP Web Services:** Spring Web Services (Spring-WS 4.0.10), WSDL4J, Jakarta XML Binding (JAXB 4)
* **REST APIs:** Spring Web MVC, Jackson JSON Provider
* **Persistence & ORM:** Spring Data JPA, Hibernate 6
* **Database:** MySQL 8.0 (InnoDB)
* **Frontend:** HTML5, CSS3 (Custom Navy/Gold Luxury Theme), JavaScript (Fetch API + DOMParser), Bootstrap 5.3
* **Testing & Tools:** Postman (REST Collection included), SoapUI (SOAP XML Requests included)

---

## 2. Architecture & Service Decomposition

The system is decomposed into 8 autonomous, loosely coupled services:

### A. Mandatory SOAP 1.1 Web Services (Contract-First / XML)
1. **Customer Registration Service:**
   * **Endpoint:** `POST /ws`
   * **WSDL:** `http://localhost:8080/ws/customers.wsdl`
   * **Target Namespace:** `http://hotel.com/soap/customer`
   * **Operations:** `RegisterCustomerRequest`, `GetCustomerDetailsRequest`
2. **Payment Processing Service (Simulated Payment Gateway):**
   * **Endpoint:** `POST /ws`
   * **WSDL:** `http://localhost:8080/ws/payments.wsdl`
   * **Target Namespace:** `http://hotel.com/soap/payment`
   * **Operations:** `ProcessPaymentRequest`, `GetPaymentDetailsRequest`

### B. RESTful JSON Micro-Services
* **Room Management Service:** `GET/POST/PUT /api/rooms`, `GET /api/rooms/available`
* **Booking Coordination Service:** `POST /api/bookings`, `GET /api/bookings`, `PUT /api/bookings/{id}/cancel`
* **Front Desk Check-In / Check-Out:** `POST /api/checkin/{id}`, `POST /api/checkout/{id}`
* **Housekeeping Service:** `GET/PUT /api/housekeeping`
* **Notification Service:** `GET /api/notifications`, `GET /api/notifications/{customerId}`
* **Authentication Service:** `POST /api/auth/login`, `POST /api/auth/register`

---

## 3. Project Directory Structure

```text
hotel-management-soa/
├── pom.xml                               # Maven project configuration
├── README.md                             # Comprehensive project manual
├── database/
│   └── hotel_management.sql             # Complete schema DDL & seed data
├── src/main/java/com/hotel/soa/
│   ├── HotelSoaApplication.java          # Spring Boot main entrypoint
│   ├── config/
│   │   ├── CorsConfig.java               # Cross-Origin resource sharing
│   │   └── WebServiceConfig.java         # Spring-WS & WSDL publisher config
│   ├── common/
│   │   ├── ApiResponse.java              # Standardized REST JSON envelope
│   │   └── GlobalExceptionHandler.java   # Centralized HTTP error handling
│   ├── customer/                         # SOAP Service 1 + REST Controller
│   ├── payment/                          # SOAP Service 2 + REST Controller
│   ├── room/                             # Room inventory & date-overlap logic
│   ├── booking/                          # Reservation lifecycle service
│   ├── checkin/                          # Front-desk operations
│   ├── housekeeping/                     # Room hygiene tracking
│   ├── notification/                     # Event-driven guest notifications
│   └── authentication/                   # User login & role management
├── src/main/resources/
│   ├── application.yml                   # Database & application properties
│   ├── schema/
│   │   ├── customer.xsd                  # XML schema contract for Customer SOAP
│   │   └── payment.xsd                   # XML schema contract for Payment SOAP
│   └── static/                           # Complete responsive frontend
│       ├── index.html                    # Public landing page
│       ├── css/style.css                 # Professional luxury theme stylesheet
│       ├── js/app.js                     # Unified SOAP XML & REST JSON client
│       └── pages/                        # Customer & Admin management pages
├── documentation/                        # Comprehensive academic architectural docs
│   ├── business-requirements.md          # BR1 to BR8 + Traceability Matrix
│   ├── soa-architecture.md               # SOA principles, catalog & comparison
│   ├── enterprise-architecture.md        # 4 EA Layers (Business, Data, App, Tech)
│   ├── business-model.md                 # Stakeholders, workflows & ROI value
│   ├── communication-workflows.md        # Sequence diagrams & payload walkthrough
│   ├── er-diagram.md                     # Database ER design & constraints
│   └── testing-guide.md                  # SoapUI & Postman step-by-step instructions
└── tests/
    ├── soap/                             # Raw SOAP 1.1 XML request/response samples
    └── rest/                             # Postman v2.1 collection & test scenarios
```

---

## 4. Setup & Execution Instructions

### Prerequisites
1. **Java Development Kit (JDK 21 or higher)**
2. **Apache Maven 3.8+**
3. **MySQL Server 8.0**

### Step 1: Database Setup
1. Start MySQL and run the initialization script:
   ```bash
   mysql -u root -p < database/hotel_management.sql
   ```
2. Verify database connection credentials in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/hotel_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
       username: root
       password: root
   ```

### Step 2: Build & Run Application
```bash
# Clean and compile
mvn clean package

# Run Spring Boot application
mvn spring-boot:run
```
The server starts on port `8080`.

---

## 5. System Access URLs

* **Public Web Portal:** `http://localhost:8080/index.html`
* **Room Search & Booking:** `http://localhost:8080/pages/search-rooms.html`
* **Customer Registration (SOAP):** `http://localhost:8080/pages/register.html`
* **Guest Bookings & Payment:** `http://localhost:8080/pages/my-bookings.html`
* **Admin Management Dashboard:** `http://localhost:8080/pages/admin/dashboard.html`
* **Customer SOAP WSDL:** `http://localhost:8080/ws/customers.wsdl`
* **Payment SOAP WSDL:** `http://localhost:8080/ws/payments.wsdl`

---

## 6. Default Demo Credentials

| Role | Username | Password | Access Level |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | Full system, rooms & reports |
| **Receptionist** | `receptionist` | `recep123` | Check-in, check-out, bookings |
| **Sample Customer** | `customer` | `cust123` | Room booking & personal history |

---

## 7. Testing Instructions

* **SOAP Web Services:** Import `http://localhost:8080/ws/customers.wsdl` and `http://localhost:8080/ws/payments.wsdl` into **SoapUI**. Sample XML request bodies are located in `tests/soap/`.
* **REST APIs:** Import `tests/rest/postman-collection.json` into **Postman** to test all REST endpoints.
