# Business Requirements Document (BRD)
## Smart Hotel Management System using Service-Oriented Architecture (SOA)

---

### 1. Executive Summary & Purpose
The Smart Hotel Management System is an enterprise-grade academic case study designed to orchestrate centralized hospitality operations through decoupled, reusable, and interoperable services. Built according to Service-Oriented Architecture (SOA) principles, the system balances standardized SOAP 1.1 Web Services (with strict WSDL/XSD contracts) for mission-critical operations with lightweight RESTful JSON micro-services for high-throughput operational interactions.

---

### 2. Business Requirements Specification

#### BR1: Customer Registration & Profile Management
* **Description:** Provides centralized customer lifecycle onboarding.
* **Actors:** Customer, Receptionist, Administrator.
* **Functional Scope:**
  - Collects customer full name, unique email address, contact phone number, and physical billing address.
  - Generates a permanent unique Customer Identification Number (`customerId`).
  - Implements contract-first validation to reject duplicate email registrations.
* **Protocol & Service:** Customer Registration Service (SOAP 1.1 XML / Spring-WS).
* **Preconditions:** Customer must provide valid email format and non-empty contact fields.
* **Postconditions:** A persistent record is committed to MySQL `customers` table; customer profile becomes referenceable across booking and payment transactions.

#### BR2: Room Inventory Management
* **Description:** Manages the lifecycle of accommodation units and room tiers.
* **Actors:** Hotel Administrator.
* **Functional Scope:**
  - Facilitates room configuration with attributes: Room Number, Category (Single, Double, Deluxe, Executive Suite), Nightly Rate, Maximum Occupancy, and Floor.
  - Updates operational status (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`).
* **Protocol & Service:** Room Management Service (REST JSON / Spring Boot).

#### BR3: Room Booking & Date-Overlap Engine
* **Description:** Handles real-time room availability discovery and reservation creation.
* **Actors:** Customer, Front-Desk Receptionist.
* **Functional Scope:**
  - Performs dynamic room availability queries evaluating date interval: `(existing_check_in < requested_check_out) AND (existing_check_out > requested_check_in)`.
  - Calculates total billing based on: `Price_Per_Night × Number_Of_Stay_Nights`.
  - Generates immutable booking reference code (`BK-{timestamp}`).
  - Sets initial booking state to `CONFIRMED`.
* **Protocol & Service:** Booking Service (REST JSON / Spring Boot).

#### BR4: Payment Processing & Financial Settlement
* **Description:** Executes simulated financial transactions for reservations.
* **Actors:** Customer, Accounts Department, Payment Gateway.
* **Functional Scope:**
  - Validates booking status and validates amount against required booking total.
  - Prevents double-charging (idempotency enforcement).
  - Supports multiple payment channels: UPI, Credit/Debit Card, and Front-Desk Cash.
  - Generates unique financial `transactionId` (`TXN{timestamp}`) and electronic `receiptNumber` (`REC{timestamp}`).
* **Protocol & Service:** Payment Processing Service (SOAP 1.1 XML / Spring-WS).

#### BR5: Guest Check-In & Check-Out Operations
* **Description:** Streamlines front-desk guest arrival and departure workflows.
* **Actors:** Receptionist, Front Office Manager.
* **Functional Scope:**
  - **Check-In:** Verifies `CONFIRMED` booking, validates customer identity, records check-in timestamp, updates booking status to `CHECKED_IN`, and shifts room state to `OCCUPIED`.
  - **Check-Out:** Verifies `CHECKED_IN` booking, confirms billing closure, records checkout timestamp, marks booking as `COMPLETED`, releases room to `AVAILABLE`, and triggers housekeeping alert.
* **Protocol & Service:** Check-In / Check-Out Service (REST JSON / Spring Boot).

#### BR6: Housekeeping & Sanitization Tracking
* **Description:** Tracks hygiene, maintenance, and turnover of accommodation units.
* **Actors:** Housekeeping Supervisor, Room Attendant.
* **Functional Scope:**
  - Categorizes cleaning state: `CLEAN`, `DIRTY`, `UNDER_MAINTENANCE`.
  - Automatically flags rooms as `DIRTY` upon guest check-out completion.
  - Assigns cleaning staff and records completion timestamps with attendant notes.
* **Protocol & Service:** Housekeeping Service (REST JSON / Spring Boot).

#### BR7: Automated Event-Driven Notifications
* **Description:** Dispatches asynchronous notification alerts to guests and staff.
* **Actors:** Customer, Receptionist, System Event Dispatcher.
* **Functional Scope:**
  - Generates notifications on: Booking Confirmation, Payment Settlement, Check-In Greeting, Check-Out Invoice, and Booking Cancellation.
  - Maintains customer notification inbox with read/unread tracking.
* **Protocol & Service:** Notification Service (REST JSON / Spring Boot).

#### BR8: Authentication & Role-Based Access Control (RBAC)
* **Description:** Secures administrative and operational endpoints.
* **Actors:** Administrator, Receptionist, Customer.
* **Functional Scope:**
  - Categorizes user permissions across `ADMIN`, `RECEPTIONIST`, and `CUSTOMER` tiers.
  - Secures system configuration and inventory editing to authorized roles.
* **Protocol & Service:** Authentication Service (REST JSON / Spring Boot).

---

### 3. Requirement Traceability Matrix (RTM)

| Requirement ID | Business Requirement Name | Implementing Service | Communication Protocol | Payload Format | Database Table(s) |
|---|---|---|---|---|---|
| **BR1** | Customer Registration | Customer Service | SOAP 1.1 HTTP | XML | `customers`, `users` |
| **BR2** | Room Management | Room Service | REST HTTP | JSON | `rooms`, `room_types` |
| **BR3** | Room Booking | Booking Service | REST HTTP | JSON | `bookings`, `rooms`, `customers` |
| **BR4** | Payment Processing | Payment Service | SOAP 1.1 HTTP | XML | `payments`, `bookings`, `customers` |
| **BR5** | Check-in / Check-out | Check-In/Out Service | REST HTTP | JSON | `check_ins`, `check_outs`, `bookings` |
| **BR6** | Housekeeping Tracking | Housekeeping Service | REST HTTP | JSON | `housekeeping`, `rooms` |
| **BR7** | Notification Management | Notification Service | REST HTTP | JSON | `notifications`, `customers` |
| **BR8** | Authentication & RBAC | Auth Service | REST HTTP | JSON | `users` |
