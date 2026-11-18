# Enterprise Architecture (EA) Specification
## Smart Hotel Management System (SOA)

---

### Layer 1: Business Architecture

#### 1. Stakeholder Ecosystem
* **Hotel Administrator:** Configures room inventory, pricing rules, user roles, and monitors macro performance.
* **Front-Desk Receptionist:** Executes check-in, verifies identity, assigns key cards, and conducts check-out billing.
* **Customer / Guest:** Onboards into the system, explores rooms, completes reservations, and pays via secure gateway.
* **Accounts & Finance Department:** Reconciles transaction logs, monitors revenue, and reviews payment receipts.
* **Housekeeping Staff:** Inspects and sanitizes rooms, updating status from `DIRTY` to `CLEAN`.

#### 2. Business Process Workflow Diagram

```mermaid
flowchart TD
    Start([Guest Decides to Stay]) --> Reg{Customer Registered?}
    Reg -- No --> SOAP_Reg["Register Guest via SOAP Service (/ws/customers)"]
    SOAP_Reg --> CustID["Acquire Customer ID"]
    Reg -- Yes --> CustID
    CustID --> Search["Search Available Rooms by Dates (REST /api/rooms/available)"]
    Search --> Select["Select Room & Review Nightly Rates"]
    Select --> Book["Create Reservation (REST POST /api/bookings)"]
    Book --> Pay["Process Payment (SOAP POST /ws/payments)"]
    Pay --> Receipt["Generate Electronic Receipt & Booking Confirmation"]
    Receipt --> Arrive["Guest Arrives at Hotel"]
    Arrive --> CheckIn["Front-Desk Check-In (REST POST /api/checkin)"]
    CheckIn --> RoomOcc["Room Status: OCCUPIED"]
    RoomOcc --> Stay["Guest Stay Duration"]
    Stay --> CheckOut["Front-Desk Check-Out (REST POST /api/checkout)"]
    CheckOut --> CleanTask["Room Status: AVAILABLE / Housekeeping: DIRTY"]
    CleanTask --> Sanitize["Housekeeping Sanitizes Unit (REST PUT /api/housekeeping)"]
    Sanitize --> RoomReady["Housekeeping: CLEAN (Ready for Next Guest)"]
    RoomReady --> End([Process Completed])
```

---

### Layer 2: Data Architecture

#### 1. Database Normalization & Integrity
The data tier is deployed on a relational MySQL 8 schema normalized to Third Normal Form (3NF). Primary key surrogate sequences (`BIGINT AUTO_INCREMENT`), foreign key references (`ON DELETE RESTRICT`), unique indexes, and enumerated domain columns guarantee referential integrity.

#### 2. Enterprise Relational ER Diagram

```mermaid
erDiagram
    USERS ||--o| CUSTOMERS : "authenticates"
    CUSTOMERS ||--o{ BOOKINGS : "places"
    ROOM_TYPES ||--o{ ROOMS : "classifies"
    ROOMS ||--o{ BOOKINGS : "reserved_in"
    BOOKINGS ||--o{ PAYMENTS : "settled_by"
    BOOKINGS ||--o| CHECK_INS : "initiates"
    BOOKINGS ||--o| CHECK_OUTS : "terminates"
    ROOMS ||--o{ HOUSEKEEPING : "inspected_in"
    CUSTOMERS ||--o{ NOTIFICATIONS : "receives"

    USERS {
        bigint id PK
        varchar username UK
        varchar password
        enum role "ADMIN, RECEPTIONIST, CUSTOMER"
        varchar email
        timestamp created_at
    }

    CUSTOMERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar phone
        varchar address
        bigint user_id FK
        timestamp created_at
    }

    ROOM_TYPES {
        bigint id PK
        varchar name UK
        text description
        decimal base_price
    }

    ROOMS {
        bigint id PK
        varchar room_number UK
        bigint room_type_id FK
        decimal price
        int capacity
        enum status "AVAILABLE, OCCUPIED, MAINTENANCE"
        int floor
    }

    BOOKINGS {
        bigint id PK
        varchar booking_id UK
        bigint customer_id FK
        bigint room_id FK
        date check_in_date
        date check_out_date
        decimal total_cost
        enum status "CONFIRMED, CANCELLED, CHECKED_IN, CHECKED_OUT, COMPLETED"
        timestamp created_at
    }

    PAYMENTS {
        bigint id PK
        varchar transaction_id UK
        bigint booking_id FK
        bigint customer_id FK
        decimal amount
        enum payment_mode "UPI, CARD, CASH"
        enum status "SUCCESS, FAILED, PENDING"
        varchar receipt_number UK
        timestamp payment_date
    }

    CHECK_INS {
        bigint id PK
        bigint booking_id FK,UK
        timestamp check_in_time
        varchar receptionist_name
        text remarks
    }

    CHECK_OUTS {
        bigint id PK
        bigint booking_id FK,UK
        timestamp check_out_time
        decimal final_amount
        varchar receptionist_name
        text remarks
    }

    HOUSEKEEPING {
        bigint id PK
        bigint room_id FK
        enum status "CLEAN, DIRTY, UNDER_MAINTENANCE"
        timestamp last_cleaned
        varchar assigned_to
        text notes
    }

    NOTIFICATIONS {
        bigint id PK
        bigint customer_id FK
        enum type "BOOKING_CONFIRMATION, PAYMENT_CONFIRMATION, CHECKIN, CHECKOUT, CANCELLATION"
        text message
        boolean is_read
        timestamp created_at
    }
```

---

### Layer 3: Application Architecture

The application tier is partitioned into 8 modular service units exposed via standardized interfaces:

```mermaid
flowchart LR
    subgraph SOAP_Domain["SOAP Enterprise Web Services Layer"]
        CS["Customer Registration Service\n(/ws/customers.wsdl)"]
        PS["Payment Processing Service\n(/ws/payments.wsdl)"]
    end

    subgraph REST_Domain["REST Operational Micro-Services Layer"]
        RS["Room Service\n(/api/rooms)"]
        BS["Booking Service\n(/api/bookings)"]
        CO["Check-In / Out Service\n(/api/checkin, checkout)"]
        HS["Housekeeping Service\n(/api/housekeeping)"]
        NS["Notification Service\n(/api/notifications)"]
        AS["Authentication Service\n(/api/auth)"]
    end

    subgraph Service_Bus["Spring Application Context (Orchestration Engine)"]
        Core["Transactional Business Logic & Entity Repositories"]
    end

    CS --> Core
    PS --> Core
    RS --> Core
    BS --> Core
    CO --> Core
    HS --> Core
    NS --> Core
    AS --> Core
```

---

### Layer 4: Technology Architecture

```mermaid
flowchart TD
    subgraph Presentation["Presentation Tier (Client-Side)"]
        Browser["Modern Web Browsers (Chrome, Firefox, Safari, Edge)"]
        HTML["HTML5 / Vanilla JavaScript (Fetch API + DOMParser)"]
        CSS["Bootstrap 5.3 + Custom Navy/Gold Stylesheet"]
    end

    subgraph Middleware["Application & Middleware Tier (Java 21 / Spring Boot 3)"]
        Tomcat["Embedded Apache Tomcat (Port 8080)"]
        SpringWS["Spring Web Services 4 (SOAP 1.1 Engine & WSDL Generator)"]
        SpringMVC["Spring Web MVC (REST Controller Framework)"]
        SpringJPA["Spring Data JPA / Hibernate 6 (ORM & Transaction Manager)"]
        JAXB["Jakarta XML Binding (JAXB 4)"]
    end

    subgraph Persistence["Database & Infrastructure Tier"]
        MySQL["MySQL Community Server 8.0 (InnoDB Engine)"]
        SchemaSQL["database/hotel_management.sql (DDL & Seeds)"]
    end

    subgraph Testing["Testing & Verification Tools"]
        SoapUITool["SoapUI (SOAP 1.1 XML Testing)"]
        PostmanTool["Postman (REST JSON Collection Testing)"]
    end

    Browser -->|HTTP Port 8080| Tomcat
    Testing -->|HTTP Port 8080| Tomcat
    Tomcat --> SpringWS
    Tomcat --> SpringMVC
    SpringWS --> JAXB
    SpringWS --> SpringJPA
    SpringMVC --> SpringJPA
    SpringJPA -->|JDBC / HikariCP| MySQL
    MySQL -.-> SchemaSQL
```

#### Technology Stack Justification
* **Java 21 LTS:** Provides virtual thread capability, modern pattern matching, and long-term enterprise platform stability.
* **Spring Boot 3.2.1:** Simplifies dependency management and application lifecycle management.
* **Spring-WS 4.0.10:** Industry benchmark for Contract-First SOAP web service development and WSDL publication.
* **MySQL 8:** Enterprise-ready ACID transactional guarantees with robust relational indexing.
* **Bootstrap 5:** Fluid responsive presentation without heavyweight framework overhead.
