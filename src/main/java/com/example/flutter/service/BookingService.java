package com.example.flutter.service;

import com.example.flutter.entities.Booking;
import com.example.flutter.entities.FixedTimeSlot;
import com.example.flutter.repositories.BookingRepository;
import com.example.flutter.repositories.FixedTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FixedTimeSlotRepository timeSlotRepository;

    public Booking bookTimeSlot(Long mentorId, Long userId, Long timeSlotId, LocalDate date, String category, String connectMethod, String googleMeetLink) {
        // Check if the time slot is already booked
        boolean isAlreadyBooked = bookingRepository
                .findByMentorIdAndBookingDate(mentorId, date)
                .stream()
                .anyMatch(booking -> booking.getTimeSlotId().equals(timeSlotId));

        if (isAlreadyBooked) {
            throw new IllegalStateException("Time slot is already booked");
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setMentorId(mentorId);
        booking.setUserId(userId);
        booking.setTimeSlotId(timeSlotId);
        booking.setBookingDate(date);
        booking.setCategory(category);  // Set category
        booking.setConnectMethod(connectMethod);  // Set connect method
        booking.setGoogleMeetLink(googleMeetLink);

        return bookingRepository.save(booking);
    }

    public List<Booking> findByUserIdAndBookingDate(Long userId, LocalDate date) {
        return bookingRepository.findByUserIdAndBookingDate(userId, date);
    }

    public List<Booking> findByMentorIdAndBookingDate(Long userId, LocalDate date) {
        return bookingRepository.findByMentorIdAndBookingDate(userId, date);
    }

    public boolean hasUpcomingBooking(Long userId, Long mentorId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Fetch all bookings for today and future
        List<Booking> bookings = bookingRepository.findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(userId, mentorId, today);

        // Remove past bookings (if any exist due to bad data)
        bookings.removeIf(booking -> booking.getBookingDate().isBefore(today));

        // Check if user has any future bookings (excluding today)
        boolean hasFutureBooking = bookings.stream().anyMatch(booking -> booking.getBookingDate().isAfter(today));

        if (hasFutureBooking) {
            return true; // Future booking exists
        }

        // Check if the only booking left is today
        Optional<Booking> todayBooking = bookings.stream().filter(booking -> booking.getBookingDate().isEqual(today)).findFirst();

        if (todayBooking.isPresent()) {
            // Fetch the time slot details
            Optional<FixedTimeSlot> timeSlotOpt = timeSlotRepository.findById(todayBooking.get().getTimeSlotId());

            if (timeSlotOpt.isPresent()) {
                FixedTimeSlot timeSlot = timeSlotOpt.get();

                // If the booked time slot end time is already past, return false
                if (timeSlot.getTimeEnd().isBefore(now)) {
                    return false;
                }
            }
            return true; // Booking is for today, and time slot is still valid
        }

        return false; // No upcoming booking
    }
}
