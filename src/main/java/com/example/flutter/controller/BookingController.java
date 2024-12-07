package com.example.flutter.controller;

import com.example.flutter.entities.Booking;
import com.example.flutter.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookTimeSlot(@RequestBody Map<String, Object> request) {
        Long mentorId = Long.valueOf(request.get("mentorId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Long timeSlotId = Long.valueOf(request.get("timeSlotId").toString());
        LocalDate date = LocalDate.parse(request.get("date").toString());
        String category = request.get("category").toString();  // Retrieve category
        String connectMethod = request.get("connectMethod").toString();  // Retrieve connect method

        Booking booking = bookingService.bookTimeSlot(mentorId, userId, timeSlotId, date, category, connectMethod);
        return ResponseEntity.ok(booking);
    }
}

