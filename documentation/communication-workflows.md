# Service Communication Workflows
## Protocol Integration: SOAP 1.1 XML & REST JSON

---

### Workflow 1: Customer Registration via SOAP 1.1 Web Service

#### Description
A new customer submits demographic onboarding details. The client serializes the data into a valid SOAP 1.1 XML envelope matching the target namespace `http://hotel.com/soap/customer`. The Spring-WS `MessageDispatcherServlet` validates the payload against `customer.xsd` and executes transactional persistence in MySQL.

#### Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Guest / Client
    participant Frontend as Browser Client (JS)
    participant SpringWS as Spring-WS (MessageDispatcherServlet)
    participant Endpoint as CustomerSoapEndpoint
    participant Service as CustomerService
    participant DB as MySQL (customers table)

    Customer->>Frontend: Enters Name, Email, Phone, Address
    Frontend->>SpringWS: HTTP POST /ws (SOAP 1.1 XML Envelope)
    Note over Frontend,SpringWS: Payload: RegisterCustomerRequest (XML)
    SpringWS->>Endpoint: Route to @PayloadRoot(RegisterCustomerRequest)
    Endpoint->>Service: registerCustomer(name, email, phone, address)
    Service->>DB: Check duplicate email
    DB-->>Service: Email is unique
    Service->>DB: INSERT INTO customers (...)
    DB-->>Service: Generated Customer ID (e.g. 101)
    Service-->>Endpoint: Customer Entity
    Endpoint-->>SpringWS: RegisterCustomerResponse JAXB Object
    SpringWS-->>Frontend: HTTP 200 OK (SOAP 1.1 XML Response Envelope)
    Note over SpringWS,Frontend: Response: RegisterCustomerResponse (status=SUCCESS, customerId=101)
    Frontend-->>Customer: Display registration confirmation & proceed to booking
```

#### Actual Wire Payloads (SOAP 1.1 XML)

##### Request XML:
```xml
POST /ws HTTP/1.1
Host: localhost:8080
Content-Type: text/xml;charset=UTF-8
SOAPAction: ""

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://hotel.com/soap/customer">
    <soapenv:Header/>
    <soapenv:Body>
        <cus:RegisterCustomerRequest>
            <cus:name>Rahul Sharma</cus:name>
            <cus:email>rahul@gmail.com</cus:email>
            <cus:phone>9876543210</cus:phone>
            <cus:address>Chennai, India</cus:address>
        </cus:RegisterCustomerRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

##### Response XML:
```xml
HTTP/1.1 200 OK
Content-Type: text/xml;charset=UTF-8

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:RegisterCustomerResponse xmlns:ns2="http://hotel.com/soap/customer">
            <ns2:customerId>101</ns2:customerId>
            <ns2:status>SUCCESS</ns2:status>
            <ns2:message>Customer registered successfully</ns2:message>
        </ns2:RegisterCustomerResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

### Workflow 2: Room Booking (REST JSON) & Payment Processing (SOAP XML)

#### Description
This composite workflow showcases dual-protocol coordination:
1. REST JSON for room availability discovery and booking placement.
2. SOAP XML for secure simulated payment settlement.
3. Event notification dispatch upon completion.

#### Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Guest
    participant UI as Browser Application
    participant REST_Room as RoomController (REST)
    participant REST_Book as BookingController (REST)
    participant SOAP_Pay as PaymentSoapEndpoint (SOAP)
    participant REST_Notif as NotificationService
    participant DB as MySQL Database

    Customer->>UI: Selects Dates (2026-12-01 to 2026-12-03)
    UI->>REST_Room: GET /api/rooms/available?checkIn=2026-12-01&checkOut=2026-12-03
    REST_Room->>DB: Query rooms without date-overlaps
    DB-->>REST_Room: Available Rooms List
    REST_Room-->>UI: 200 OK (JSON Rooms Array)

    Customer->>UI: Selects Room 101 & Clicks "Confirm Booking"
    UI->>REST_Book: POST /api/bookings (JSON: customerId, roomId, dates)
    REST_Book->>DB: INSERT INTO bookings (status='CONFIRMED', total_cost=5000.00)
    DB-->>REST_Book: Created Booking (id=501, bookingId='BK-1700000000')
    REST_Book->>REST_Notif: createNotification(customerId, BOOKING_CONFIRMATION)
    REST_Book-->>UI: 201 Created (JSON: booking details)

    Customer->>UI: Selects Payment Mode (UPI) & Submits Payment
    UI->>SOAP_Pay: POST /ws (SOAP XML ProcessPaymentRequest: bookingId=501, amount=5000.00)
    SOAP_Pay->>DB: Verify booking #501 exists & not yet paid
    SOAP_Pay->>DB: INSERT INTO payments (status='SUCCESS', txn_id='TXN1001')
    DB-->>SOAP_Pay: Committed
    SOAP_Pay-->>UI: 200 OK (SOAP XML ProcessPaymentResponse: transactionId=TXN1001, receiptNumber=REC1001)
    UI-->>Customer: Render Receipt & Confirmed Booking Card
```

#### Actual Payloads

##### Booking Placement Request (REST JSON):
```json
POST /api/bookings HTTP/1.1
Content-Type: application/json

{
  "customerId": 101,
  "roomId": 1,
  "checkInDate": "2026-12-01",
  "checkOutDate": "2026-12-03"
}
```

##### Booking Placement Response (REST JSON):
```json
HTTP/1.1 201 Created
Content-Type: application/json

{
  "status": 201,
  "message": "Booking created successfully",
  "data": {
    "id": 501,
    "bookingId": "BK-1700000000123",
    "customer": { "id": 101, "name": "Rahul Sharma" },
    "room": { "id": 1, "roomNumber": "101", "price": 2500.00 },
    "checkInDate": "2026-12-01",
    "checkOutDate": "2026-12-03",
    "totalCost": 5000.00,
    "status": "CONFIRMED"
  }
}
```

##### Payment Request (SOAP 1.1 XML):
```xml
POST /ws HTTP/1.1
Content-Type: text/xml;charset=UTF-8

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pay="http://hotel.com/soap/payment">
    <soapenv:Header/>
    <soapenv:Body>
        <pay:ProcessPaymentRequest>
            <pay:bookingId>501</pay:bookingId>
            <pay:customerId>101</pay:customerId>
            <pay:amount>5000.00</pay:amount>
            <pay:paymentMode>UPI</pay:paymentMode>
        </pay:ProcessPaymentRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

##### Payment Response (SOAP 1.1 XML):
```xml
HTTP/1.1 200 OK
Content-Type: text/xml;charset=UTF-8

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:ProcessPaymentResponse xmlns:ns2="http://hotel.com/soap/payment">
            <ns2:transactionId>TXN1700000000999</ns2:transactionId>
            <ns2:status>SUCCESS</ns2:status>
            <ns2:message>Payment processed successfully</ns2:message>
            <ns2:receiptNumber>REC1700000000999</ns2:receiptNumber>
        </ns2:ProcessPaymentResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

### Workflow 3: Check-In, Check-Out & Housekeeping Operations (REST JSON)

#### Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Receptionist as Front-Desk Staff
    participant FrontCtrl as CheckInOutController (REST)
    participant CheckService as CheckInOutService
    participant HKService as HousekeepingService
    participant DB as MySQL Database

    Note over Receptionist,DB: Phase A: Guest Arrival & Check-In
    Receptionist->>FrontCtrl: POST /api/checkin/501 (receptionistName="Priya", remarks="ID Verified")
    FrontCtrl->>CheckService: checkIn(501, "Priya", "ID Verified")
    CheckService->>DB: INSERT INTO check_ins (...)
    CheckService->>DB: UPDATE bookings SET status='CHECKED_IN' WHERE id=501
    CheckService->>DB: UPDATE rooms SET status='OCCUPIED' WHERE id=1
    DB-->>CheckService: Success
    CheckService-->>FrontCtrl: CheckIn Entity
    FrontCtrl-->>Receptionist: 200 OK (CheckIn Confirmed)

    Note over Receptionist,DB: Phase B: Guest Departure & Check-Out
    Receptionist->>FrontCtrl: POST /api/checkout/501 (receptionistName="Priya", remarks="Key returned")
    FrontCtrl->>CheckService: checkOut(501, "Priya", "Key returned")
    CheckService->>DB: INSERT INTO check_outs (final_amount=5000.00)
    CheckService->>DB: UPDATE bookings SET status='COMPLETED' WHERE id=501
    CheckService->>DB: UPDATE rooms SET status='AVAILABLE' WHERE id=1
    CheckService->>HKService: Mark Room 1 as DIRTY
    HKService->>DB: INSERT / UPDATE housekeeping SET status='DIRTY'
    DB-->>CheckService: Success
    CheckService-->>FrontCtrl: CheckOut Entity
    FrontCtrl-->>Receptionist: 200 OK (CheckOut Completed)
```
