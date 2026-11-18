package com.hotel.soa.room.service;

import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.entity.Room.RoomStatus;
import com.hotel.soa.room.entity.RoomType;
import com.hotel.soa.room.repository.RoomRepository;
import com.hotel.soa.room.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return roomRepository.findByStatus(RoomStatus.AVAILABLE);
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }

    @Transactional
    public Room addRoom(String roomNumber, Long roomTypeId, BigDecimal price, Integer capacity, Integer floor) {
        if (roomRepository.existsByRoomNumber(roomNumber)) {
            throw new IllegalArgumentException("Room number " + roomNumber + " already exists");
        }
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Room type not found with id: " + roomTypeId));

        Room room = Room.builder()
                .roomNumber(roomNumber)
                .roomType(roomType)
                .price(price != null ? price : roomType.getBasePrice())
                .capacity(capacity != null ? capacity : 2)
                .floor(floor != null ? floor : 1)
                .status(RoomStatus.AVAILABLE)
                .build();

        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, String roomNumber, Long roomTypeId, BigDecimal price, Integer capacity, Integer floor, String statusStr) {
        Room room = getRoomById(id);
        if (roomNumber != null && !roomNumber.equals(room.getRoomNumber())) {
            if (roomRepository.existsByRoomNumber(roomNumber)) {
                throw new IllegalArgumentException("Room number " + roomNumber + " already exists");
            }
            room.setRoomNumber(roomNumber);
        }
        if (roomTypeId != null) {
            RoomType roomType = roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Room type not found: " + roomTypeId));
            room.setRoomType(roomType);
        }
        if (price != null) room.setPrice(price);
        if (capacity != null) room.setCapacity(capacity);
        if (floor != null) room.setFloor(floor);
        if (statusStr != null) {
            try {
                room.setStatus(RoomStatus.valueOf(statusStr.toUpperCase()));
            } catch (Exception ignored) {}
        }
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoomStatus(Long id, RoomStatus status) {
        Room room = getRoomById(id);
        room.setStatus(status);
        return roomRepository.save(room);
    }

    public List<RoomType> getRoomTypes() {
        return roomTypeRepository.findAll();
    }
}
