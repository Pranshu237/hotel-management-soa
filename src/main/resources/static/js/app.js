/**
 * Smart Hotel Management System - JavaScript Client
 * Handles both REST JSON APIs and SOAP XML Web Services
 */

const API_BASE = '/api';
const SOAP_BASE = '/ws';

// Utilities
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    const bgClass = type === 'success' ? 'bg-success' : (type === 'danger' ? 'bg-danger' : 'bg-warning');
    toast.className = `toast align-items-center text-white ${bgClass} border-0 show mb-2`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    container.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, 4500);
}

function getCurrentUser() {
    try {
        const u = sessionStorage.getItem('hotel_user');
        return u ? JSON.parse(u) : null;
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    sessionStorage.setItem('hotel_user', JSON.stringify(user));
}

function logout() {
    sessionStorage.removeItem('hotel_user');
    window.location.href = '/pages/login.html';
}

function checkAdminAuth() {
    const user = getCurrentUser();
    if (!user || (user.role !== 'ADMIN' && user.role !== 'RECEPTIONIST')) {
        alert('Access denied. Please log in with staff credentials.');
        window.location.href = '/pages/login.html';
    }
}

// ==========================================
// SOAP XML Web Services Clients
// ==========================================

async function sendSoapRequest(xmlPayload) {
    const response = await fetch(SOAP_BASE, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/xml;charset=UTF-8',
            'SOAPAction': '""'
        },
        body: xmlPayload
    });
    const text = await response.text();
    const parser = new DOMParser();
    return parser.parseFromString(text, 'text/xml');
}

/**
 * SOAP Service 1: Customer Registration
 */
async function registerCustomerSOAP(name, email, phone, address) {
    const xml = `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:cus="http://hotel.com/soap/customer">
    <soapenv:Header/>
    <soapenv:Body>
        <cus:RegisterCustomerRequest>
            <cus:name>${escapeXml(name)}</cus:name>
            <cus:email>${escapeXml(email)}</cus:email>
            <cus:phone>${escapeXml(phone)}</cus:phone>
            <cus:address>${escapeXml(address)}</cus:address>
        </cus:RegisterCustomerRequest>
    </soapenv:Body>
</soapenv:Envelope>`;

    const doc = await sendSoapRequest(xml);
    const statusElem = doc.getElementsByTagNameNS('*', 'status')[0] || doc.getElementsByTagName('status')[0];
    const messageElem = doc.getElementsByTagNameNS('*', 'message')[0] || doc.getElementsByTagName('message')[0];
    const customerIdElem = doc.getElementsByTagNameNS('*', 'customerId')[0] || doc.getElementsByTagName('customerId')[0];

    return {
        status: statusElem ? statusElem.textContent : 'UNKNOWN',
        message: messageElem ? messageElem.textContent : 'No response message',
        customerId: customerIdElem ? customerIdElem.textContent : null
    };
}

/**
 * SOAP Service 2: Payment Processing
 */
async function processPaymentSOAP(bookingId, customerId, amount, paymentMode) {
    const xml = `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pay="http://hotel.com/soap/payment">
    <soapenv:Header/>
    <soapenv:Body>
        <pay:ProcessPaymentRequest>
            <pay:bookingId>${bookingId}</pay:bookingId>
            <pay:customerId>${customerId}</pay:customerId>
            <pay:amount>${amount}</pay:amount>
            <pay:paymentMode>${escapeXml(paymentMode)}</pay:paymentMode>
        </pay:ProcessPaymentRequest>
    </soapenv:Body>
</soapenv:Envelope>`;

    const doc = await sendSoapRequest(xml);
    const statusElem = doc.getElementsByTagNameNS('*', 'status')[0] || doc.getElementsByTagName('status')[0];
    const messageElem = doc.getElementsByTagNameNS('*', 'message')[0] || doc.getElementsByTagName('message')[0];
    const txnElem = doc.getElementsByTagNameNS('*', 'transactionId')[0] || doc.getElementsByTagName('transactionId')[0];
    const receiptElem = doc.getElementsByTagNameNS('*', 'receiptNumber')[0] || doc.getElementsByTagName('receiptNumber')[0];

    return {
        status: statusElem ? statusElem.textContent : 'UNKNOWN',
        message: messageElem ? messageElem.textContent : 'No response message',
        transactionId: txnElem ? txnElem.textContent : null,
        receiptNumber: receiptElem ? receiptElem.textContent : null
    };
}

function escapeXml(unsafe) {
    if (!unsafe) return '';
    return unsafe.replace(/[<>&'"]/g, function (c) {
        switch (c) {
            case '<': return '&lt;';
            case '>': return '&gt;';
            case '&': return '&amp;';
            case '\'': return '&apos;';
            case '"': return '&quot;';
        }
    });
}

// ==========================================
// REST JSON APIs
// ==========================================

async function fetchJSON(url, options = {}) {
    options.headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    const res = await fetch(url, options);
    const json = await res.json();
    if (!res.ok && json && json.message) {
        throw new Error(json.message);
    }
    return json;
}

// Rooms
async function getRooms() {
    const res = await fetchJSON(`${API_BASE}/rooms`);
    return res.data || [];
}

async function getAvailableRooms(checkIn, checkOut) {
    let url = `${API_BASE}/rooms/available`;
    if (checkIn && checkOut) {
        url += `?checkIn=${checkIn}&checkOut=${checkOut}`;
    }
    const res = await fetchJSON(url);
    return res.data || [];
}

async function getRoomTypes() {
    const res = await fetchJSON(`${API_BASE}/rooms/types`);
    return res.data || [];
}

async function addRoom(data) {
    return await fetchJSON(`${API_BASE}/rooms`, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

async function updateRoom(id, data) {
    return await fetchJSON(`${API_BASE}/rooms/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// Bookings
async function createBooking(data) {
    return await fetchJSON(`${API_BASE}/bookings`, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

async function getBookings() {
    const res = await fetchJSON(`${API_BASE}/bookings`);
    return res.data || [];
}

async function getCustomerBookings(customerId) {
    const res = await fetchJSON(`${API_BASE}/bookings/customer/${customerId}`);
    return res.data || [];
}

async function cancelBooking(bookingId) {
    return await fetchJSON(`${API_BASE}/bookings/${bookingId}/cancel`, {
        method: 'PUT'
    });
}

// Check-in / Check-out
async function checkInGuest(bookingId, receptionistName, remarks) {
    return await fetchJSON(`${API_BASE}/checkin/${bookingId}`, {
        method: 'POST',
        body: JSON.stringify({ receptionistName, remarks })
    });
}

async function checkOutGuest(bookingId, receptionistName, remarks) {
    return await fetchJSON(`${API_BASE}/checkout/${bookingId}`, {
        method: 'POST',
        body: JSON.stringify({ receptionistName, remarks })
    });
}

// Housekeeping
async function getHousekeeping() {
    const res = await fetchJSON(`${API_BASE}/housekeeping`);
    return res.data || [];
}

async function updateHousekeeping(roomId, data) {
    return await fetchJSON(`${API_BASE}/housekeeping/${roomId}`, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// Customers & Payments (REST)
async function getCustomers() {
    const res = await fetchJSON(`${API_BASE}/customers`);
    return res.data || [];
}

async function getPayments() {
    const res = await fetchJSON(`${API_BASE}/payments`);
    return res.data || [];
}

// Notifications
async function getCustomerNotifications(customerId) {
    const res = await fetchJSON(`${API_BASE}/notifications/${customerId}`);
    return res.data || [];
}

async function getAllNotifications() {
    const res = await fetchJSON(`${API_BASE}/notifications`);
    return res.data || [];
}

// Authentication
async function loginUser(username, password) {
    const res = await fetchJSON(`${API_BASE}/auth/login`, {
        method: 'POST',
        body: JSON.stringify({ username, password })
    });
    return res.data;
}

// ==========================================
// Page Load Handlers
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    // Update logged in user badge if exists
    const user = getCurrentUser();
    const userBadge = document.getElementById('user-badge');
    if (userBadge) {
        if (user) {
            userBadge.innerHTML = `<span class="badge bg-warning text-dark me-2">${user.role}</span> ${user.username} <button class="btn btn-sm btn-outline-light ms-2" onclick="logout()">Logout</button>`;
        } else {
            userBadge.innerHTML = `<a href="/pages/login.html" class="btn btn-sm btn-outline-warning">Staff / Guest Login</a>`;
        }
    }
});
