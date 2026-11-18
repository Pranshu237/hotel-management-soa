package com.hotel.soa.checkin.repository;

import com.hotel.soa.checkin.entity.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {

    Optional<CheckOut> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}
