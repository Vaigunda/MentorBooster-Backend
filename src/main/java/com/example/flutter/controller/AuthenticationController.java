package com.example.flutter.controller;

import com.example.flutter.entities.Mentor;
import com.example.flutter.entities.Users;
import com.example.flutter.response.ApiResponse;
import com.example.flutter.response.LoginResponse;
import com.example.flutter.service.AuthenticationService;
import com.example.flutter.service.JwtService;
import com.example.flutter.service.MentorService;
import com.example.flutter.service.UserDetailsServiceImpl;
import com.example.flutter.util.CommonFiles;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RequestMapping("/api/auth")
@Slf4j
@RestController
public class AuthenticationController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsServiceImpl userService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private CommonFiles commonFiles;

    @Autowired
    private MentorService mentorService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody Users user) {
        log.info("Attempting login for user: {}", user.getEmailId());

        try {
            Users authenticatedUser = authenticationService.authenticate(user);

            // Generate JWT token
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    authenticatedUser.getUserName(),
                    authenticatedUser.getPassword(),
                    new ArrayList<>()
            );
            String jwtToken = jwtService.generateToken(userDetails);

            Long id = authenticatedUser.getUserType().equals("Mentor")
                    ? mentorService.findByEmail(authenticatedUser.getEmailId()).getId()
                    : authenticatedUser.getId();

            LoginResponse loginResponse = new LoginResponse(
                    jwtToken,
                    jwtService.getExpirationTime(),
                    id,
                    authenticatedUser.getName(),
                    authenticatedUser.getUserType()
            );

            log.info("User logged in successfully: {}", authenticatedUser.getEmailId());
            return ResponseEntity.ok(loginResponse);

        } catch (UsernameNotFoundException ex) {
            log.warn("Login failed: {}", ex.getMessage());
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            log.error("Unexpected error during login for email {}: {}", user.getEmailId(), ex.getMessage());
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> createUser(@Valid @RequestBody Users user) {
        try {
            if (userService.existsByEmail(user.getEmailId())) {
                log.warn("Sign-up failed: Email {} already in use", user.getEmailId());
                return ResponseEntity.ok("Email already Exists");
            }

            // Check if username exists
            if (userService.existsByUser(user.getUserName())) {
                log.warn("Sign-up failed: Username {} already in use", user.getUserName());
                return ResponseEntity.ok("Username already Exists");
            }

            // Instantiate a PasswordEncoder (you could also inject this via @Autowired)
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());

            // Set the hashed password back to the user object
            user.setPassword(hashedPassword);
            user.setUserType("User");

            // Save the user to the database
            this.userService.save(user);

            // Return a success message with HTTP status CREATED (201)
            return new ResponseEntity<>("Sign Up Success", HttpStatus.CREATED);
        } catch (Exception ex) {
            log.debug("Exception occurred while creating user: " + ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/mail/verify/{email}")
    public ResponseEntity<ApiResponse> sendVerificationEmail(@Valid @PathVariable String  email) {
        String otp = commonFiles.generateOTP(6);
        commonFiles.sendOTPUser(email,otp);
        ApiResponse response = new ApiResponse(otp);
        return ResponseEntity.ok(response);
    }

}