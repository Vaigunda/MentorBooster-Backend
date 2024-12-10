package com.example.flutter.controller;

import com.example.flutter.entities.Users;
import com.example.flutter.response.LoginResponse;
import com.example.flutter.service.AuthenticationService;
import com.example.flutter.service.JwtService;
import com.example.flutter.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RequestMapping("/auth")
@Slf4j
@RestController
public class AuthenticationController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsServiceImpl userService;

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
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(), authenticatedUser.getId(), authenticatedUser.getName());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> createUser(@Valid @RequestBody Users user) {
        try {
            // Instantiate a PasswordEncoder (you could also inject this via @Autowired)
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Hash the password
            String hashedPassword = passwordEncoder.encode(user.getPassword());

            // Set the hashed password back to the user object
            user.setPassword(hashedPassword);

            // Save the user to the database
            this.userService.save(user);

            // Return a success message with HTTP status CREATED (201)
            return new ResponseEntity<>("Sign Up Success", HttpStatus.CREATED);
        } catch (Exception ex) {
            log.debug("Exception occurred while creating user: " + ex.getMessage());
            throw ex;
        }
    }

}