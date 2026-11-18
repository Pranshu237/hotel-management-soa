package com.hotel.soa.booking.controller;

import com.hotel.soa.booking.entity.Booking;
import com.hotel.soa.booking.service.BookingService;
import com.hotel.soa.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        Long roomId = Long.valueOf(body.get("roomId").toString());
        LocalDate checkIn = LocalDate.parse(body.get("checkInDate").toString());
        LocalDate checkOut = LocalDate.parse(body.get("checkOutDate").toString());

        Booking booking = bookingService.createBooking(customerId, roomId, checkIn, checkOut);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Booking created successfully", booking));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getAllBookings()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingById(id)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByCustomerId(customerId)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long id) {
        Booking cancelled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", cancelled));
    }
}
