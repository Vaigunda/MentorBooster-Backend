package com.example.flutter.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "certificates")  // Ensure the table name matches the one in your database
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key, auto-generated

    @Column(name = "name", nullable = false)  // Ensure the column name matches
    private String name;  // Name of the certificate

    @Column(name = "provide_by")  // Ensure the column name matches
    private String provideBy;  // Who provided the certificate

    @Column(name = "create_date")  // Ensure the column name matches
    private String createDate;  // The date the certificate was issued

    @Column(name = "image_url")  // Ensure the column name matches
    private String imageUrl;  // URL of the certificate image


    @ManyToOne  // Many certificates can belong to one mentor
    @JoinColumn(name = "mentor_id")
    @JsonBackReference // Foreign key column in the certificates table
    private Mentor mentor; // The mentor associated with this certificate

    @JsonProperty("mentor_id")
    public Long getMentorId() {
        return mentor != null ? mentor.getId() : null;
    }

}
