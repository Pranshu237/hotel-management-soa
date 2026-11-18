package com.hotel.soa.room.repository;

import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.entity.Room.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomTypeId(Long roomTypeId);

    List<Room> findByFloor(Integer floor);

    boolean existsByRoomNumber(String roomNumber);

    /**
     * Find rooms that are AVAILABLE and not booked (CONFIRMED or CHECKED_IN)
     * for the given date range. A booking overlaps if its check-in is before
     * the requested check-out AND its check-out is after the requested check-in.
     */
    @Query("SELECT r FROM Room r WHERE r.status <> 'MAINTENANCE' " +
           "AND r.id NOT IN (" +
           "  SELECT b.room.id FROM Booking b " +
           "  WHERE b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "  AND b.checkInDate < :checkOutDate " +
           "  AND b.checkOutDate > :checkInDate" +
           ")")
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * Find available rooms filtered by room type for the given date range.
     */
    @Query("SELECT r FROM Room r WHERE r.status <> 'MAINTENANCE' " +
           "AND r.roomType.id = :roomTypeId " +
           "AND r.id NOT IN (" +
           "  SELECT b.room.id FROM Booking b " +
           "  WHERE b.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "  AND b.checkInDate < :checkOutDate " +
           "  AND b.checkOutDate > :checkInDate" +
           ")")
    List<Room> findAvailableRoomsByType(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
