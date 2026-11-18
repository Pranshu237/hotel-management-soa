package com.hotel.soa.payment.service;

import com.hotel.soa.booking.entity.Booking;
import com.hotel.soa.booking.repository.BookingRepository;
import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.repository.CustomerRepository;
import com.hotel.soa.payment.entity.Payment;
import com.hotel.soa.payment.entity.Payment.PaymentMode;
import com.hotel.soa.payment.entity.Payment.PaymentStatus;
import com.hotel.soa.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Payment processPayment(Long bookingId, Long customerId, BigDecimal amount, String paymentModeStr) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        // Check duplicate payment
        List<Payment> existingPayments = paymentRepository.findByBookingIdAndStatus(bookingId, PaymentStatus.SUCCESS);
        if (!existingPayments.isEmpty()) {
            throw new IllegalStateException("Payment already completed for booking id: " + bookingId);
        }

        // Validate amount against booking total cost if set
        if (booking.getTotalCost() != null && amount.compareTo(booking.getTotalCost()) < 0) {
            throw new IllegalArgumentException("Payment amount (" + amount + ") is less than total booking cost (" + booking.getTotalCost() + ")");
        }

        PaymentMode mode;
        try {
            mode = PaymentMode.valueOf(paymentModeStr.toUpperCase().trim());
        } catch (Exception e) {
            mode = PaymentMode.CARD;
        }

        long timestamp = System.currentTimeMillis();
        String txnId = "TXN" + timestamp;
        String receiptNo = "REC" + timestamp;

        Payment payment = Payment.builder()
                .transactionId(txnId)
                .booking(booking)
                .customer(customer)
                .amount(amount)
                .paymentMode(mode)
                .status(PaymentStatus.SUCCESS)
                .receiptNumber(receiptNo)
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
