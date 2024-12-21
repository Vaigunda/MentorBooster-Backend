package com.example.flutter.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false) // Added join column
    @JsonBackReference
    private Mentor mentor;

    @JsonProperty("mentor_id")
    public Long getMentorId() {
        return mentor != null ? mentor.getId() : null;
    }

    private String message;

    private  String createDate;

    private String createdById;


}
