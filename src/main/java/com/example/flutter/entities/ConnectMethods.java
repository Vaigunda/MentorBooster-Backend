package com.example.flutter.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "connect_methods")
public class ConnectMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
}
