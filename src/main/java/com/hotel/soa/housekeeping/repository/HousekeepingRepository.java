package com.hotel.soa.housekeeping.repository;

import com.hotel.soa.housekeeping.entity.Housekeeping;
import com.hotel.soa.housekeeping.entity.Housekeeping.CleaningStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {

    Optional<Housekeeping> findByRoomId(Long roomId);

    List<Housekeeping> findByStatus(CleaningStatus status);

    List<Housekeeping> findByAssignedTo(String assignedTo);
}
