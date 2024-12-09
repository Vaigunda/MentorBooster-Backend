package com.example.flutter.controller;

import com.example.flutter.entities.Users;
import com.example.flutter.response.LoginResponse;
import com.example.flutter.service.AuthenticationService;
import com.example.flutter.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody Users user){
        Users authenticatedUser = authenticationService.authenticate(user);
        if (authenticatedUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                new ArrayList<>());
        String jwtToken = jwtService.generateToken(userDetails);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(), authenticatedUser.getId());
        return ResponseEntity.ok(loginResponse);
    }
}