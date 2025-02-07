package com.example.flutter.controller;

import com.example.flutter.dto.BookingDTO;
import com.example.flutter.entities.Booking;
import com.example.flutter.entities.FixedTimeSlot;
import com.example.flutter.entities.Mentor;
import com.example.flutter.entities.Users;
import com.example.flutter.service.BookingService;
import com.example.flutter.service.MentorService;
import com.example.flutter.service.TimeSlotService;
import com.example.flutter.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private UserDetailsServiceImpl detailsService;

    @Autowired
    private MentorService mentorService;

    @PostMapping
    public ResponseEntity<?> bookTimeSlot(@RequestBody Map<String, Object> request) {

        Long mentorId = Long.valueOf(request.get("mentorId").toString());
        Long userId = Long.valueOf(request.get("userId").toString());
        Long timeSlotId = Long.valueOf(request.get("timeSlotId").toString());
        LocalDate date = LocalDate.parse(request.get("date").toString());
        String category = request.get("category").toString();  // Retrieve category
        String connectMethod = request.get("connectMethod").toString();
        String googleMeetLink = request.get("googleMeetLink") != null ? request.get("googleMeetLink").toString() : null;

        List<Booking> bookings = bookingService.findByUserIdAndBookingDate(userId, date);
        FixedTimeSlot currentSlot = timeSlotService.findById(timeSlotId);
        for (Booking booking : bookings) {
            FixedTimeSlot slot = timeSlotService.findById(booking.getTimeSlotId());
            // Check if the time slots overlap
            boolean isOverlapping = currentSlot.getTimeStart().isBefore(slot.getTimeEnd())
                    && slot.getTimeStart().isBefore(currentSlot.getTimeEnd());

            if (isOverlapping) {
                return ResponseEntity.badRequest().body("Already your booking the same time slot for another booking.");
            }
        }

        Booking booking = bookingService.bookTimeSlot(mentorId, userId, timeSlotId, date, category, connectMethod, googleMeetLink);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}/{date}")
    public List<BookingDTO> getBookingsByUser(@PathVariable Long userId, @PathVariable LocalDate date) {
        List<Booking> bookings =  this.bookingService.findByUserIdAndBookingDate(userId, date);

        List<BookingDTO> bookingDTOS = new ArrayList<>();
        for (Booking booking : bookings) {
            FixedTimeSlot slot = timeSlotService.findById(booking.getTimeSlotId());
            Mentor mentor = mentorService.findById(booking.getMentorId());

            BookingDTO dto = new BookingDTO();
            dto.setBookingDate(booking.getBookingDate());
            dto.setId(booking.getId());
            dto.setCategory(booking.getCategory());
            dto.setConnectMethod(booking.getConnectMethod());
            dto.setTimeSlotStart(slot.getTimeStart());
            dto.setTimeSlotEnd(slot.getTimeEnd());
            dto.setMentorName(mentor.getName());
            dto.setGMeetLink(booking.getGoogleMeetLink());
            dto.setMentorId(mentor.getId());
            dto.setUserId(userId);

            bookingDTOS.add(dto);
        }
        return bookingDTOS;
    }

    @GetMapping("/mentor/{mentorId}/{date}")
    public List<BookingDTO> getBookingsByMentor(@PathVariable Long mentorId, @PathVariable LocalDate date) {
        List<Booking> bookings =  this.bookingService.findByMentorIdAndBookingDate(mentorId, date);

        List<BookingDTO> bookingDTOS = new ArrayList<>();
        for (Booking booking : bookings) {
            FixedTimeSlot slot = timeSlotService.findById(booking.getTimeSlotId());
            Users users = detailsService.findById(booking.getUserId());

            BookingDTO dto = new BookingDTO();
            dto.setBookingDate(booking.getBookingDate());
            dto.setId(booking.getId());
            dto.setCategory(booking.getCategory());
            dto.setConnectMethod(booking.getConnectMethod());
            dto.setTimeSlotStart(slot.getTimeStart());
            dto.setTimeSlotEnd(slot.getTimeEnd());
            dto.setUserName(users.getName());
            dto.setGMeetLink(booking.getGoogleMeetLink());
            dto.setMentorId(mentorId);
            dto.setUserId(users.getId());

            bookingDTOS.add(dto);
        }
        return bookingDTOS;
    }

    @GetMapping("/check")
    public boolean hasUpcomingBooking(@RequestParam Long userId, @RequestParam Long mentorId) {
        return bookingService.hasUpcomingBooking(userId, mentorId);
    }
}

