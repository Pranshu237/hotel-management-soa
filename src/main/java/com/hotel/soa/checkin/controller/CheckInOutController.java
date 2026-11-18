package com.hotel.soa.checkin.controller;

import com.hotel.soa.checkin.entity.CheckIn;
import com.hotel.soa.checkin.entity.CheckOut;
import com.hotel.soa.checkin.service.CheckInOutService;
import com.hotel.soa.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    public CheckInOutController(CheckInOutService checkInOutService) {
        this.checkInOutService = checkInOutService;
    }

    @PostMapping("/checkin/{bookingId}")
    public ResponseEntity<ApiResponse<CheckIn>> checkIn(
            @PathVariable Long bookingId,
            @RequestBody(required = false) Map<String, Object> body) {
        String receptionistName = body != null && body.get("receptionistName") != null ?
                body.get("receptionistName").toString() : "Receptionist";
        String remarks = body != null && body.get("remarks") != null ?
                body.get("remarks").toString() : "Check-in completed";

        CheckIn checkIn = checkInOutService.checkIn(bookingId, receptionistName, remarks);
        return ResponseEntity.ok(ApiResponse.success("Guest checked in successfully", checkIn));
    }

    @PostMapping("/checkout/{bookingId}")
    public ResponseEntity<ApiResponse<CheckOut>> checkOut(
            @PathVariable Long bookingId,
            @RequestBody(required = false) Map<String, Object> body) {
        String receptionistName = body != null && body.get("receptionistName") != null ?
                body.get("receptionistName").toString() : "Receptionist";
        String remarks = body != null && body.get("remarks") != null ?
                body.get("remarks").toString() : "Check-out completed";

        CheckOut checkOut = checkInOutService.checkOut(bookingId, receptionistName, remarks);
        return ResponseEntity.ok(ApiResponse.success("Guest checked out successfully", checkOut));
    }

    @GetMapping("/checkins")
    public ResponseEntity<ApiResponse<List<CheckIn>>> getAllCheckIns() {
        return ResponseEntity.ok(ApiResponse.success(checkInOutService.getAllCheckIns()));
    }

    @GetMapping("/checkouts")
    public ResponseEntity<ApiResponse<List<CheckOut>>> getAllCheckOuts() {
        return ResponseEntity.ok(ApiResponse.success(checkInOutService.getAllCheckOuts()));
    }
}
