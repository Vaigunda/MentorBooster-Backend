package com.example.flutter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MentorResponseDTO {
    private String id;
    private String name;
    private String avatar_url;
    private Double rate;
    private Integer number_of_mentoree;
    private List<String> category_names;
}
