package com.example.flutter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MentorDTO {
    private Long id;
    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private String role;
    private Boolean verified;
    private Double rate;
    private Integer numberOfMentoree;
    private FreeDTO free;
    private List<CertificateDTO> certificates;
    private List<ExperienceDTO> experiences;
    private List<ReviewDTO> reviews;
    private List<TimeSlotDTO> timeSlots;
    private List<CategoryDTO> categories;

    // Getters and Setters
}
