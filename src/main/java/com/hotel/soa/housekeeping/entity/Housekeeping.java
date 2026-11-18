package com.hotel.soa.housekeeping.entity;

import com.hotel.soa.room.entity.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "housekeeping")
public class Housekeeping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private CleaningStatus status = CleaningStatus.CLEAN;

    @Column(name = "last_cleaned")
    private LocalDateTime lastCleaned;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum CleaningStatus {
        CLEAN,
        DIRTY,
        UNDER_MAINTENANCE
    }
}
