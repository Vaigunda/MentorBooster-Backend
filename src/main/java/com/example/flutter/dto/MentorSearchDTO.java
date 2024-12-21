package com.example.flutter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MentorSearchDTO {
    private Long mentorId;
    private String name;
    private Integer mentees;
    private Double rating;
    private List<String> skills;
    private String role;
    private String avatar;
}
