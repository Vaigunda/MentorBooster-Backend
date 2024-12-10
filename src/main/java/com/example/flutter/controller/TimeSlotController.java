package com.example.flutter.controller;

import com.example.flutter.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RestController
@RequestMapping("/api/mentors")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping("/time-slots/{mentorId}")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable Long mentorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(timeSlotService.getAvailableTimeSlots(mentorId, date));
    }
}

