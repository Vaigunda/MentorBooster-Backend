package com.example.flutter.service;

import com.example.flutter.entities.Booking;
import com.example.flutter.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking bookTimeSlot(Long mentorId, Long userId, Long timeSlotId, LocalDate date, String category, String connectMethod) {
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

        return bookingRepository.save(booking);
    }
}
