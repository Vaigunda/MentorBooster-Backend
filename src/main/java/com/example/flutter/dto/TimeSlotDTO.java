package com.example.flutter.dto;

import lombok.Data;

@Data
public class TimeSlotDTO {
    private Long id;
    private String timeStart;
    private String timeEnd;
    private Long mentorId;

    // Getters and Setters
}
