package com.hotel.soa.housekeeping.service;

import com.hotel.soa.housekeeping.entity.Housekeeping;
import com.hotel.soa.housekeeping.entity.Housekeeping.CleaningStatus;
import com.hotel.soa.housekeeping.repository.HousekeepingRepository;
import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HousekeepingService {

    private final HousekeepingRepository housekeepingRepository;
    private final RoomRepository roomRepository;

    public HousekeepingService(HousekeepingRepository housekeepingRepository,
                               RoomRepository roomRepository) {
        this.housekeepingRepository = housekeepingRepository;
        this.roomRepository = roomRepository;
    }

    public List<Housekeeping> getAllRecords() {
        return housekeepingRepository.findAll();
    }

    public Housekeeping getByRoomId(Long roomId) {
        return housekeepingRepository.findByRoomId(roomId).orElseGet(() -> {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
            Housekeeping hk = Housekeeping.builder()
                    .room(room)
                    .status(CleaningStatus.CLEAN)
                    .lastCleaned(LocalDateTime.now())
                    .build();
            return housekeepingRepository.save(hk);
        });
    }

    @Transactional
    public Housekeeping updateStatus(Long roomId, String statusStr, String assignedTo, String notes) {
        CleaningStatus cleaningStatus = CleaningStatus.valueOf(statusStr.toUpperCase().trim());

        Housekeeping hk = housekeepingRepository.findByRoomId(roomId).orElseGet(() -> {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
            return Housekeeping.builder().room(room).build();
        });

        hk.setStatus(cleaningStatus);
        if (assignedTo != null && !assignedTo.isEmpty()) hk.setAssignedTo(assignedTo);
        if (notes != null) hk.setNotes(notes);
        if (cleaningStatus == CleaningStatus.CLEAN) {
            hk.setLastCleaned(LocalDateTime.now());
        }

        return housekeepingRepository.save(hk);
    }
}
