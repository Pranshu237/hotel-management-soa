-- ============================================================
-- Hotel Management SOA - Database Schema
-- MySQL 8.x Compatible
-- ============================================================

CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST', 'CUSTOMER') NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: customers
-- ============================================================
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(255),
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: room_types
-- ============================================================
CREATE TABLE IF NOT EXISTS room_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: rooms
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    capacity INT NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    floor INT NOT NULL,
    CONSTRAINT fk_rooms_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: bookings
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_cost DECIMAL(10, 2),
    status ENUM('CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'CHECKED_OUT', 'COMPLETED') DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: payments
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(30) UNIQUE NOT NULL,
    booking_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_mode ENUM('UPI', 'CARD', 'CASH') NOT NULL,
    status ENUM('SUCCESS', 'FAILED', 'PENDING') DEFAULT 'PENDING',
    receipt_number VARCHAR(30) UNIQUE,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payments_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: check_ins
-- ============================================================
CREATE TABLE IF NOT EXISTS check_ins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    check_in_time TIMESTAMP NOT NULL,
    receptionist_name VARCHAR(100),
    remarks TEXT,
    CONSTRAINT fk_checkins_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: check_outs
-- ============================================================
CREATE TABLE IF NOT EXISTS check_outs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    check_out_time TIMESTAMP NOT NULL,
    final_amount DECIMAL(10, 2),
    receptionist_name VARCHAR(100),
    remarks TEXT,
    CONSTRAINT fk_checkouts_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: housekeeping
-- ============================================================
CREATE TABLE IF NOT EXISTS housekeeping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    status ENUM('CLEAN', 'DIRTY', 'UNDER_MAINTENANCE') DEFAULT 'CLEAN',
    last_cleaned TIMESTAMP,
    assigned_to VARCHAR(100),
    notes TEXT,
    CONSTRAINT fk_housekeeping_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE: notifications
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    type ENUM('BOOKING_CONFIRMATION', 'PAYMENT_CONFIRMATION', 'CHECKIN', 'CHECKOUT', 'CANCELLATION') NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_room ON bookings(room_id);
CREATE INDEX idx_bookings_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_customer ON payments(customer_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_room_type ON rooms(room_type_id);
CREATE INDEX idx_notifications_customer ON notifications(customer_id);
CREATE INDEX idx_housekeeping_room ON housekeeping(room_id);

-- ============================================================
-- SAMPLE DATA
-- ============================================================

-- Users (passwords are plaintext here; in production, store hashed)
INSERT INTO users (username, password, role, email) VALUES
('admin', 'admin123', 'ADMIN', 'admin@hotelmanagement.com'),
('receptionist1', 'recep123', 'RECEPTIONIST', 'receptionist1@hotelmanagement.com'),
('john_doe', 'john123', 'CUSTOMER', 'john.doe@email.com'),
('jane_smith', 'jane123', 'CUSTOMER', 'jane.smith@email.com');

-- Room Types
INSERT INTO room_types (name, description, base_price) VALUES
('SINGLE', 'Cozy single room with one single bed, work desk, and en-suite bathroom. Ideal for solo travelers.', 2500.00),
('DOUBLE', 'Spacious double room with one double bed or two single beds, seating area, and modern amenities.', 4500.00),
('DELUXE', 'Premium deluxe room with king-size bed, lounge area, mini-bar, city view, and luxury bathroom.', 7500.00),
('SUITE', 'Luxurious suite with separate living room, king-size bed, jacuzzi, panoramic view, and butler service.', 15000.00);

-- Rooms (10 rooms, at least 2 of each type)
INSERT INTO rooms (room_number, room_type_id, price, capacity, status, floor) VALUES
('101', 1, 2500.00, 1, 'AVAILABLE', 1),
('102', 1, 2500.00, 1, 'AVAILABLE', 1),
('201', 2, 4500.00, 2, 'AVAILABLE', 2),
('202', 2, 4500.00, 2, 'AVAILABLE', 2),
('203', 2, 4800.00, 2, 'AVAILABLE', 2),
('301', 3, 7500.00, 2, 'AVAILABLE', 3),
('302', 3, 7500.00, 2, 'AVAILABLE', 3),
('303', 3, 8000.00, 3, 'AVAILABLE', 3),
('401', 4, 15000.00, 4, 'AVAILABLE', 4),
('402', 4, 15000.00, 4, 'AVAILABLE', 4);

-- Customers
INSERT INTO customers (name, email, phone, address, user_id) VALUES
('John Doe', 'john.doe@email.com', '+919876543210', '123 MG Road, Bangalore, Karnataka 560001', 3),
('Jane Smith', 'jane.smith@email.com', '+919876543211', '456 Park Street, Kolkata, West Bengal 700016', 4);

-- Housekeeping (one record per room)
INSERT INTO housekeeping (room_id, status, last_cleaned, assigned_to, notes) VALUES
(1, 'CLEAN', NOW(), 'Housekeeping Team A', 'Room freshly prepared'),
(2, 'CLEAN', NOW(), 'Housekeeping Team A', 'Room freshly prepared'),
(3, 'CLEAN', NOW(), 'Housekeeping Team A', 'Room freshly prepared'),
(4, 'CLEAN', NOW(), 'Housekeeping Team B', 'Room freshly prepared'),
(5, 'CLEAN', NOW(), 'Housekeeping Team B', 'Room freshly prepared'),
(6, 'CLEAN', NOW(), 'Housekeeping Team B', 'Room freshly prepared'),
(7, 'CLEAN', NOW(), 'Housekeeping Team C', 'Room freshly prepared'),
(8, 'CLEAN', NOW(), 'Housekeeping Team C', 'Room freshly prepared'),
(9, 'CLEAN', NOW(), 'Housekeeping Team C', 'Room freshly prepared'),
(10, 'CLEAN', NOW(), 'Housekeeping Team C', 'Room freshly prepared');
