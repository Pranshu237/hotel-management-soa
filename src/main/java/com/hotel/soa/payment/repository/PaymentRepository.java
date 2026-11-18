package com.hotel.soa.payment.repository;

import com.hotel.soa.payment.entity.Payment;
import com.hotel.soa.payment.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByCustomerId(Long customerId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByReceiptNumber(String receiptNumber);

    List<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus status);

    boolean existsByTransactionId(String transactionId);

    boolean existsByReceiptNumber(String receiptNumber);
}
