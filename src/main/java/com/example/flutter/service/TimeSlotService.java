package com.example.flutter.service;

import com.example.flutter.entities.Booking;
import com.example.flutter.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeSlotService {

    @Autowired
    private JdbcTemplate jdbcTemplate; // Use JdbcTemplate for raw SQL queries

    @Autowired
    private BookingRepository bookingRepository;

    // Get available time slots for a mentor on a specific date
    public List<Map<String, Object>> getAvailableTimeSlots(Long mentorId, LocalDate date) {
        // Define a formatter for hh:mm
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Fetch all fixed time slots for the mentor using JdbcTemplate
        String sql = "SELECT id, time_start, time_end FROM fixed_time_slots WHERE mentor_id = ?";
        List<Map<String, Object>> fixedSlots = jdbcTemplate.query(sql, new Object[]{mentorId}, (rs, rowNum) -> {
            Map<String, Object> slot = new HashMap<>();
            slot.put("id", rs.getLong("id"));
            slot.put("timeStart", rs.getString("time_start"));
            slot.put("timeEnd", rs.getString("time_end"));
            return slot;
        });

        // Fetch all bookings for the mentor on the given date
        List<Booking> bookings = bookingRepository.findByMentorIdAndBookingDate(mentorId, date);

        // Create a set of booked time slot IDs
        Set<Long> bookedSlotIds = bookings.stream()
                .map(Booking::getTimeSlotId)
                .collect(Collectors.toSet());

        // Prepare the response
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        for (Map<String, Object> slot : fixedSlots) {
            Map<String, Object> slotData = new HashMap<>();
            Long id = (Long) slot.get("id");
            String timeStart = (String) slot.get("timeStart");
            String timeEnd = (String) slot.get("timeEnd");

            slotData.put("id", id);

            // Format timeStart and timeEnd using the formatter
            slotData.put("timeStart", LocalTime.parse(timeStart).format(timeFormatter));
            slotData.put("timeEnd", LocalTime.parse(timeEnd).format(timeFormatter));

            slotData.put("status", bookedSlotIds.contains(id) ? "occupied" : "available");
            timeSlots.add(slotData);
        }

        return timeSlots;
    }
}