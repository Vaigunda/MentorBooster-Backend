package com.example.flutter.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;
    private Long userId;

    public LoginResponse(String token, long expiresIn, Long userId) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
    }
}