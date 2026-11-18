package com.hotel.soa.room.controller;

import com.hotel.soa.common.ApiResponse;
import com.hotel.soa.room.entity.Room;
import com.hotel.soa.room.entity.RoomType;
import com.hotel.soa.room.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Room>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Room>>> getAvailableRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        List<Room> available = roomService.getAvailableRooms(checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Room>> addRoom(@RequestBody Map<String, Object> body) {
        String roomNumber = (String) body.get("roomNumber");
        Long roomTypeId = Long.valueOf(body.get("roomTypeId").toString());
        BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : null;
        Integer capacity = body.get("capacity") != null ? Integer.valueOf(body.get("capacity").toString()) : null;
        Integer floor = body.get("floor") != null ? Integer.valueOf(body.get("floor").toString()) : null;

        Room room = roomService.addRoom(roomNumber, roomTypeId, price, capacity, floor);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Room added successfully", room));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Room>> updateRoom(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String roomNumber = body.get("roomNumber") != null ? body.get("roomNumber").toString() : null;
        Long roomTypeId = body.get("roomTypeId") != null ? Long.valueOf(body.get("roomTypeId").toString()) : null;
        BigDecimal price = body.get("price") != null ? new BigDecimal(body.get("price").toString()) : null;
        Integer capacity = body.get("capacity") != null ? Integer.valueOf(body.get("capacity").toString()) : null;
        Integer floor = body.get("floor") != null ? Integer.valueOf(body.get("floor").toString()) : null;
        String statusStr = body.get("status") != null ? body.get("status").toString() : null;

        Room updated = roomService.updateRoom(id, roomNumber, roomTypeId, price, capacity, floor, statusStr);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", updated));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<RoomType>>> getRoomTypes() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomTypes()));
    }
}
