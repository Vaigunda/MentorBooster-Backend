package com.example.flutter.service;

import com.example.flutter.dto.BookingRequestDTO;
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

    public Booking bookTimeSlot(BookingRequestDTO requestDTO) {
        // Check if the time slot is already booked
        boolean isAlreadyBooked = bookingRepository
                .findByMentorIdAndBookingDate(requestDTO.getMentorId(), LocalDate.parse(requestDTO.getDate()))
                .stream()
                .anyMatch(booking -> booking.getTimeSlotId().equals(requestDTO.getTimeSlotId()));

        if (isAlreadyBooked) {
            throw new IllegalStateException("Time slot is already booked");
        }

        // Map DTO to Booking entity
        Booking booking = new Booking();
        booking.setMentorId(requestDTO.getMentorId());
        booking.setUserId(requestDTO.getUserId());
        booking.setTimeSlotId(requestDTO.getTimeSlotId());
        booking.setBookingDate(LocalDate.parse(requestDTO.getDate()));
        booking.setCategory(requestDTO.getCategory());
        booking.setConnectMethod(requestDTO.getConnectMethod());

        // Save booking
        return bookingRepository.save(booking);
    }

    public List<Booking> findByUserIdAndBookingDate(Long userId, LocalDate date) {
        return bookingRepository.findByUserIdAndBookingDate(userId, date);
    }

    public List<Booking> findByMentorIdAndBookingDate(Long userId, LocalDate date) {
        return bookingRepository.findByMentorIdAndBookingDate(userId, date);
    }
}
