# Enterprise Testing & Verification Guide
## Smart Hotel Management System (SOA)

---

### Part 1: SOAP Web Services Testing (SoapUI / cURL)

#### 1. WSDL Service Contracts
When the application is running, open a browser or SoapUI and verify the generated WSDL definitions:
* **Customer Service WSDL:** `http://localhost:8080/ws/customers.wsdl`
* **Payment Service WSDL:** `http://localhost:8080/ws/payments.wsdl`

---

#### 2. Test Case SOAP-01: Register Customer
* **Endpoint:** `POST http://localhost:8080/ws`
* **Header:** `Content-Type: text/xml;charset=UTF-8`
* **SOAPAction:** `""`

##### Request XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://hotel.com/soap/customer">
    <soapenv:Header/>
    <soapenv:Body>
        <cus:RegisterCustomerRequest>
            <cus:name>Rahul Sharma</cus:name>
            <cus:email>rahul.sharma@example.com</cus:email>
            <cus:phone>9876543210</cus:phone>
            <cus:address>Chennai, Tamil Nadu, India</cus:address>
        </cus:RegisterCustomerRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

##### Expected Response XML:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:RegisterCustomerResponse xmlns:ns2="http://hotel.com/soap/customer">
            <ns2:customerId>1</ns2:customerId>
            <ns2:status>SUCCESS</ns2:status>
            <ns2:message>Customer registered successfully</ns2:message>
        </ns2:RegisterCustomerResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

#### 3. Test Case SOAP-02: Get Customer Details
* **Endpoint:** `POST http://localhost:8080/ws`
* **Header:** `Content-Type: text/xml;charset=UTF-8`

##### Request XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://hotel.com/soap/customer">
    <soapenv:Header/>
    <soapenv:Body>
        <cus:GetCustomerDetailsRequest>
            <cus:customerId>1</cus:customerId>
        </cus:GetCustomerDetailsRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

---

#### 4. Test Case SOAP-03: Process Payment
* **Endpoint:** `POST http://localhost:8080/ws`
* **Header:** `Content-Type: text/xml;charset=UTF-8`

##### Request XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pay="http://hotel.com/soap/payment">
    <soapenv:Header/>
    <soapenv:Body>
        <pay:ProcessPaymentRequest>
            <pay:bookingId>1</pay:bookingId>
            <pay:customerId>1</pay:customerId>
            <pay:amount>5000.00</pay:amount>
            <pay:paymentMode>UPI</pay:paymentMode>
        </pay:ProcessPaymentRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

##### Expected Response XML:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:ProcessPaymentResponse xmlns:ns2="http://hotel.com/soap/payment">
            <ns2:transactionId>TXN1700000000</ns2:transactionId>
            <ns2:status>SUCCESS</ns2:status>
            <ns2:message>Payment processed successfully</ns2:message>
            <ns2:receiptNumber>REC1700000000</ns2:receiptNumber>
        </ns2:ProcessPaymentResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

#### 5. Test Case SOAP-04: Get Payment Details
* **Endpoint:** `POST http://localhost:8080/ws`
* **Header:** `Content-Type: text/xml;charset=UTF-8`

##### Request XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pay="http://hotel.com/soap/payment">
    <soapenv:Header/>
    <soapenv:Body>
        <pay:GetPaymentDetailsRequest>
            <pay:transactionId>TXN1700000000</pay:transactionId>
        </pay:GetPaymentDetailsRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

---

### Part 2: RESTful JSON APIs Testing (Postman / cURL)

#### 1. Authentication
* **POST** `http://localhost:8080/api/auth/login`
```json
{
  "username": "admin",
  "password": "admin123"
}
```
* **Status:** `200 OK`

---

#### 2. Search Available Rooms
* **GET** `http://localhost:8080/api/rooms/available?checkIn=2026-12-01&checkOut=2026-12-03`
* **Status:** `200 OK`

---

#### 3. Create Booking
* **POST** `http://localhost:8080/api/bookings`
```json
{
  "customerId": 1,
  "roomId": 1,
  "checkInDate": "2026-12-01",
  "checkOutDate": "2026-12-03"
}
```
* **Status:** `201 Created`

---

#### 4. Guest Check-In
* **POST** `http://localhost:8080/api/checkin/1`
```json
{
  "receptionistName": "Priya Sharma",
  "remarks": "ID verified. Key assigned."
}
```
* **Status:** `200 OK`

---

#### 5. Guest Check-Out
* **POST** `http://localhost:8080/api/checkout/1`
```json
{
  "receptionistName": "Priya Sharma",
  "remarks": "Keys returned. Room released."
}
```
* **Status:** `200 OK`

---

#### 6. Update Housekeeping Status
* **PUT** `http://localhost:8080/api/housekeeping/1`
```json
{
  "status": "CLEAN",
  "assignedTo": "Ramesh Kumar",
  "notes": "Full sanitization complete."
}
```
* **Status:** `200 OK`
