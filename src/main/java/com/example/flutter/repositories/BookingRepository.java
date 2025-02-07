package com.example.flutter.repositories;

import com.example.flutter.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByMentorIdAndBookingDate(Long mentorId, LocalDate date);

    List<Booking> findByUserIdAndBookingDate(Long userId, LocalDate date);

    List<Booking> findByUserIdAndMentorIdAndBookingDateGreaterThanEqual(Long userId, Long mentorId, LocalDate date);
}

