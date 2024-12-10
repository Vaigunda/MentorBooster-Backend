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

    @Column(name = "user_name", unique = true) // Maps to the 'user_name' column in the DB
    private String userName;

    @Column(name = "password") // Maps to the 'password' column in the DB
    private String password;

    @Column(name = "name") // Maps to the 'name' column in the DB
    private String name;

    @Column(name = "email_id", unique = true) // Maps to the 'email_id' column in the DB
    private String emailId;

    @Column(name = "age") // Maps to the 'age' column in the DB
    private int age;

    @Column(name = "gender") // Maps to the 'gender' column in the DB
    private String gender;
}
