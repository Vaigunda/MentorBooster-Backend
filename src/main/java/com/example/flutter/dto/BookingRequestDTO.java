package com.example.flutter.dto;



import lombok.Data;


@Data
public class BookingRequestDTO {

    private Long mentorId;


    private Long userId;


    private Long timeSlotId;

    private String date;


    private String category;


    private String connectMethod;
}

