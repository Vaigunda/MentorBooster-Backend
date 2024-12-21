package com.example.flutter.dto;

import lombok.Data;

@Data
public class ExperienceDTO {
    private Long id;
    private String role;
    private String company_name;
    private String start_date;
    private String end_date;
    private String description;
    private Long mentor_id;

    // Getters and Setters
}
