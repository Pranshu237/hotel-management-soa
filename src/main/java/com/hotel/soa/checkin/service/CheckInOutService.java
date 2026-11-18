package com.hotel.soa.checkin.service;

import com.hotel.soa.booking.entity.Booking;
import com.hotel.soa.booking.entity.Booking.BookingStatus;
import com.hotel.soa.booking.repository.BookingRepository;
import com.hotel.soa.checkin.entity.CheckIn;
import com.hotel.soa.checkin.entity.CheckOut;
import com.hotel.soa.checkin.repository.CheckInRepository;
import com.hotel.soa.checkin.repository.CheckOutRepository;
import com.hotel.soa.housekeeping.entity.Housekeeping;
import com.hotel.soa.housekeeping.entity.Housekeeping.CleaningStatus;
import com.hotel.soa.housekeeping.repository.HousekeepingRepository;
import com.hotel.soa.notification.entity.Notification.NotificationType;
import com.hotel.soa.notification.service.NotificationService;
import com.hotel.soa.room.entity.Room.RoomStatus;
import com.hotel.soa.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInOutService {

    private final CheckInRepository checkInRepository;
    private final CheckOutRepository checkOutRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HousekeepingRepository housekeepingRepository;
    private final NotificationService notificationService;

    public CheckInOutService(CheckInRepository checkInRepository,
                             CheckOutRepository checkOutRepository,
                             BookingRepository bookingRepository,
                             RoomRepository roomRepository,
                             HousekeepingRepository housekeepingRepository,
                             NotificationService notificationService) {
        this.checkInRepository = checkInRepository;
        this.checkOutRepository = checkOutRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public CheckIn checkIn(Long bookingId, String receptionistName, String remarks) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only CONFIRMED bookings can be checked in. Current status: " + booking.getStatus());
        }

        CheckIn checkIn = CheckIn.builder()
                .booking(booking)
                .checkInTime(LocalDateTime.now())
                .receptionistName(receptionistName != null ? receptionistName : "Front Desk")
                .remarks(remarks)
                .build();

        CheckIn saved = checkInRepository.save(checkIn);

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);

        booking.getRoom().setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(booking.getRoom());

        try {
            notificationService.createNotification(
                    booking.getCustomer().getId(),
                    NotificationType.CHECKIN,
                    "Welcome! You have checked into Room " + booking.getRoom().getRoomNumber() + " at Grand Hotel."
            );
        } catch (Exception ignored) {}

        return saved;
    }

    @Transactional
    public CheckOut checkOut(Long bookingId, String receptionistName, String remarks) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Booking must be CHECKED_IN to process check-out. Current status: " + booking.getStatus());
        }

        CheckOut checkOut = CheckOut.builder()
                .booking(booking)
                .checkOutTime(LocalDateTime.now())
                .finalAmount(booking.getTotalCost())
                .receptionistName(receptionistName != null ? receptionistName : "Front Desk")
                .remarks(remarks)
                .build();

        CheckOut saved = checkOutRepository.save(checkOut);

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(booking.getRoom());

        // Update or create housekeeping status as DIRTY
        Housekeeping hk = housekeepingRepository.findByRoomId(booking.getRoom().getId())
                .orElseGet(() -> Housekeeping.builder()
                        .room(booking.getRoom())
                        .build());
        hk.setStatus(CleaningStatus.DIRTY);
        hk.setNotes("Guest checked out from booking " + booking.getBookingId());
        housekeepingRepository.save(hk);

        try {
            notificationService.createNotification(
                    booking.getCustomer().getId(),
                    NotificationType.CHECKOUT,
                    "Thank you for staying at Grand Hotel! Your check-out for Room " + booking.getRoom().getRoomNumber() + " is complete."
            );
        } catch (Exception ignored) {}

        return saved;
    }

    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    public List<CheckOut> getAllCheckOuts() {
        return checkOutRepository.findAll();
    }
}
