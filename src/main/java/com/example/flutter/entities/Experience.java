package com.example.flutter.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "experiences") // Explicit table name mapping
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;



    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    @JsonBackReference
    private Mentor mentor;

    @JsonProperty("mentor_id")
    public Long getMentorId() {
        return mentor != null ? mentor.getId() : null;
    }

}
