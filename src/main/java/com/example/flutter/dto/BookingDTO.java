package com.example.flutter.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDTO {
    private Long id;

    private Long mentorId;

    private String mentorName;

    private Long userId;

    private String userName;

    private LocalTime timeSlotStart;

    private LocalTime timeSlotEnd;

    private LocalDate bookingDate;

    private String category;

    private String connectMethod;

    private String gMeetLink;
}
