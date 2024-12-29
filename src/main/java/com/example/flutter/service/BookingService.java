package com.example.flutter.service;

import com.example.flutter.entities.Booking;
import com.example.flutter.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

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
}
