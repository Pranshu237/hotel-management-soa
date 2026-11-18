package com.hotel.soa.payment.controller;

import com.hotel.soa.common.ApiResponse;
import com.hotel.soa.payment.entity.Payment;
import com.hotel.soa.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByBookingId(bookingId)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByCustomerId(customerId)));
    }
}
