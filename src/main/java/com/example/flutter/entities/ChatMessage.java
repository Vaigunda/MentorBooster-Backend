package com.example.flutter.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long recipientId;

    private String content;

    private boolean read; // Default: message is unread

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}

