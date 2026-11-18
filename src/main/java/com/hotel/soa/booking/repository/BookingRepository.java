package com.hotel.soa.booking.repository;

import com.hotel.soa.booking.entity.Booking;
import com.hotel.soa.booking.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByCustomerIdAndStatus(Long customerId, BookingStatus status);

    /**
     * Check for overlapping bookings on the same room that are CONFIRMED or CHECKED_IN.
     * Two date ranges overlap when: existingCheckIn < newCheckOut AND existingCheckOut > newCheckIn.
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.checkInDate < :checkOutDate " +
           "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * Find overlapping bookings excluding a specific booking (useful for updates).
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.id <> :excludeBookingId " +
           "AND b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND b.checkInDate < :checkOutDate " +
           "AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookingsExcluding(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("excludeBookingId") Long excludeBookingId
    );

    /**
     * Find bookings by check-in date range.
     */
    @Query("SELECT b FROM Booking b WHERE b.checkInDate BETWEEN :startDate AND :endDate")
    List<Booking> findByCheckInDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByBookingId(String bookingId);
}
