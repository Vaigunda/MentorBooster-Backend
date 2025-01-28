package com.example.flutter.entities;

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
    private Mentor mentor;

    private String message;

    private Long rating;

    private String createDate;

    private String createdById;

    private String userName;
}
