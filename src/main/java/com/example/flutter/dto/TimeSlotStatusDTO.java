package com.example.flutter.dto;

import lombok.Data;

@Data
public class TimeSlotStatusDTO {
    private Long id;
    private String timeStart;
    private String timeEnd;
    private String status;
}
