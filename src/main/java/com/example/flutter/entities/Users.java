package com.example.flutter.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // Matches the table name in the database
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    private String password;

    private String name;

    @Column(unique = true)
    private String emailId;

    private Integer age;

    private String gender;

    private String userType;
}
