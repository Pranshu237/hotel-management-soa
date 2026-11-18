# Automated & Manual Test Scenarios
## Smart Hotel Management System (SOA)

---

### Scenario 1: End-to-End Guest Lifecycle Workflow
This scenario verifies complete dual-protocol (SOAP + REST) interoperability.

1. **Step 1: Register Customer via SOAP Web Service**
   * **Protocol:** SOAP 1.1 XML
   * **Endpoint:** `POST http://localhost:8080/ws`
   * **Payload:** `tests/soap/customer-registration-test.xml`
   * **Expected Result:** XML response with `status: SUCCESS` and `customerId: 1`.

2. **Step 2: Search Available Rooms**
   * **Protocol:** REST JSON
   * **Endpoint:** `GET http://localhost:8080/api/rooms/available?checkIn=2026-12-01&checkOut=2026-12-03`
   * **Expected Result:** HTTP 200 with list containing Room 101 (`price: 2500.00`).

3. **Step 3: Create Booking**
   * **Protocol:** REST JSON
   * **Endpoint:** `POST http://localhost:8080/api/bookings`
   * **Payload:**
     ```json
     { "customerId": 1, "roomId": 1, "checkInDate": "2026-12-01", "checkOutDate": "2026-12-03" }
     ```
   * **Expected Result:** HTTP 201 Created with `id: 1`, `status: CONFIRMED`, `totalCost: 5000.00`.

4. **Step 4: Process Payment via SOAP Web Service**
   * **Protocol:** SOAP 1.1 XML
   * **Endpoint:** `POST http://localhost:8080/ws`
   * **Payload:** `tests/soap/payment-process-test.xml` (using `bookingId: 1`, `amount: 5000.00`)
   * **Expected Result:** XML response with `status: SUCCESS`, generated `transactionId`, and `receiptNumber`.

5. **Step 5: Front-Desk Check-In**
   * **Protocol:** REST JSON
   * **Endpoint:** `POST http://localhost:8080/api/checkin/1`
   * **Payload:**
     ```json
     { "receptionistName": "Priya Sharma", "remarks": "ID verified" }
     ```
   * **Expected Result:** HTTP 200 OK, Booking transitions to `CHECKED_IN`, Room transitions to `OCCUPIED`.

6. **Step 6: Front-Desk Check-Out**
   * **Protocol:** REST JSON
   * **Endpoint:** `POST http://localhost:8080/api/checkout/1`
   * **Payload:**
     ```json
     { "receptionistName": "Priya Sharma", "remarks": "Keys returned" }
     ```
   * **Expected Result:** HTTP 200 OK, Booking transitions to `COMPLETED`, Room transitions to `AVAILABLE`, Room is flagged as `DIRTY` in Housekeeping.

7. **Step 7: Housekeeping Sanitization**
   * **Protocol:** REST JSON
   * **Endpoint:** `PUT http://localhost:8080/api/housekeeping/1`
   * **Payload:**
     ```json
     { "status": "CLEAN", "assignedTo": "Ramesh Kumar", "notes": "Sanitized and linens replaced" }
     ```
   * **Expected Result:** HTTP 200 OK, Room status marked `CLEAN`.

---

### Scenario 2: Duplicate Overlapping Booking Prevention
1. Execute a booking for Room 101 between `2026-12-01` and `2026-12-03`.
2. Attempt a second booking for Room 101 with overlapping dates (`2026-12-02` to `2026-12-04`).
3. **Expected Result:** HTTP 409 Conflict / Error with message: *"Room 101 is already booked for the selected dates"*.

---

### Scenario 3: Double Payment Prevention (Idempotency)
1. Submit `ProcessPaymentRequest` via SOAP for `bookingId: 1`.
2. Once processed successfully, re-submit the same request for `bookingId: 1`.
3. **Expected Result:** SOAP Response with `status: FAILURE` and message: *"Payment already completed for booking id: 1"*.
