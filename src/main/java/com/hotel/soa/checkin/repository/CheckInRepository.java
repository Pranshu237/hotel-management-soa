package com.hotel.soa.checkin.repository;

import com.hotel.soa.checkin.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}
