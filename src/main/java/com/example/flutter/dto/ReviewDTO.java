package com.example.flutter.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String message;
    private String create_date;
    private String created_by_id;
    private Long mentor_id;

    // Getters and Setters
}
