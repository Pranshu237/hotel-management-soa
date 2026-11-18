package com.hotel.soa.booking.service;

import com.hotel.soa.booking.entity.Booking;
import com.hotel.soa.booking.entity.Booking.BookingStatus;
import com.hotel.soa.booking.repository.BookingRepository;
import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.repository.CustomerRepository;
import com.hotel.soa.notification.entity.Notification.NotificationType;
import com.hotel.soa.notification.service.NotificationService;
import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          CustomerRepository customerRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Booking createBooking(Long customerId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        // Check overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(roomId, checkIn, checkOut);
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is already booked for the selected dates");
        }

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) days = 1;
        BigDecimal totalCost = room.getPrice().multiply(BigDecimal.valueOf(days));

        String bookingId = "BK-" + System.currentTimeMillis();

        Booking booking = Booking.builder()
                .bookingId(bookingId)
                .customer(customer)
                .room(room)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .totalCost(totalCost)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Notify customer
        try {
            notificationService.createNotification(
                    customer.getId(),
                    NotificationType.BOOKING_CONFIRMATION,
                    "Your booking " + bookingId + " for Room " + room.getRoomNumber() + " (" + checkIn + " to " + checkOut + ") is confirmed! Total: ₹" + totalCost
            );
        } catch (Exception ignored) {}

        return saved;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public Booking getBookingByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with booking ID: " + bookingId));
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel an active or completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        // Notify customer
        try {
            notificationService.createNotification(
                    booking.getCustomer().getId(),
                    NotificationType.CANCELLATION,
                    "Your booking " + booking.getBookingId() + " has been cancelled."
            );
        } catch (Exception ignored) {}

        return saved;
    }
}
