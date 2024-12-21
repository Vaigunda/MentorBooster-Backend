package com.example.flutter.dto;


import lombok.Data;

@Data
public class CertificateDTO {
    private Long id;
    private String name;
    private String provide_by;
    private String create_date;
    private String image_url;
    private Long mentor_id;

    // Getters and Setters
}
