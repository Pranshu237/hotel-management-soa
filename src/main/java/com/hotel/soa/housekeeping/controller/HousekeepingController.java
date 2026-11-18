package com.hotel.soa.housekeeping.controller;

import com.hotel.soa.common.ApiResponse;
import com.hotel.soa.housekeeping.entity.Housekeeping;
import com.hotel.soa.housekeeping.service.HousekeepingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/housekeeping")
@CrossOrigin(origins = "*")
public class HousekeepingController {

    private final HousekeepingService housekeepingService;

    public HousekeepingController(HousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Housekeeping>>> getAllHousekeeping() {
        return ResponseEntity.ok(ApiResponse.success(housekeepingService.getAllRecords()));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Housekeeping>> getByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(housekeepingService.getByRoomId(roomId)));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Housekeeping>> updateStatus(
            @PathVariable Long roomId,
            @RequestBody Map<String, Object> body) {
        String status = body.get("status") != null ? body.get("status").toString() : "CLEAN";
        String assignedTo = body.get("assignedTo") != null ? body.get("assignedTo").toString() : null;
        String notes = body.get("notes") != null ? body.get("notes").toString() : null;

        Housekeeping updated = housekeepingService.updateStatus(roomId, status, assignedTo, notes);
        return ResponseEntity.ok(ApiResponse.success("Housekeeping status updated", updated));
    }
}
