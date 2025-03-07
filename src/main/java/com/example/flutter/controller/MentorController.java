package com.example.flutter.controller;

import com.example.flutter.entities.*;
import com.example.flutter.service.UserDetailsServiceImpl;
import com.example.flutter.service.UsersService;
import com.example.flutter.util.CommonFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import com.example.flutter.service.DatabaseService;

import com.example.flutter.service.MentorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@Slf4j
@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private MentorService mentorService;

    @Autowired
    private UsersService usersService;

    @Autowired
    CommonFiles commonFiles;

    // Get all mentors including their time slots
    @GetMapping("/all")
    public List<Map<String, Object>> getAllMentors() {
        return mentorService.getAllMentors();
    }

    @GetMapping
    public  List<Map<String, Object>> getAllMentorsInfo() {
        return databaseService.getAllMentorsInfo();
    }

    @PostMapping
    public ResponseEntity<String> createMentor(@RequestBody Mentor mentor) {
        try {

            // Check if the mentor's email already exists
            if (mentorService.existsByEmail(mentor.getEmail())) {
                return ResponseEntity.ok("Email already Exists");
            }

            // Call the service to add the mentor
            String responseMessage = mentorService.addMentor(mentor);

            if (responseMessage.equals("Mentor added successfully!")) {
                // Instantiate a PasswordEncoder (you could also inject this via @Autowired)
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String password = commonFiles.generateAlphaPassword(6);
                String hashedPassword = passwordEncoder.encode(password);

                //Create User after Mentor create
                Users users = new Users();
                users.setName(mentor.getName());
                users.setEmailId(mentor.getEmail());
                users.setUserName(commonFiles.generateAlphaPassword(6));
                users.setUserType("Mentor");
                users.setPassword(hashedPassword);

                Users newMentor = usersService.save(users);
                if (newMentor != null) {
                    //Send Email for Mentor
                    commonFiles.sendPasswordToMentor(mentor, password);
                }
            }
            // Return HTTP 201 with the success message
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error (optional but recommended)
            e.printStackTrace();
            // Return a bad request response in case of error
            return new ResponseEntity<>("Failed to add mentor. Please check the input data.", HttpStatus.BAD_REQUEST);
        }
    }

    // Update Mentor Info
    @PutMapping("/{mentorId}")
    public String updateMentor(@PathVariable Long mentorId, @RequestBody Mentor updatedMentor) {
        int updateCount = mentorService.updateMentorInfo(mentorId, updatedMentor.getName(), updatedMentor.getGender(), updatedMentor.getEmail(), updatedMentor.getAvatarUrl(),
                updatedMentor.getBio(), updatedMentor.getRole(), updatedMentor.getFreePrice(),
                updatedMentor.getFreeUnit(), updatedMentor.getVerified(), updatedMentor.getRate(),
                updatedMentor.getNumberOfMentoree());

        if (updateCount == 1) {
            // Proceed with updating fixed time slots, certificates, experiences, categories if the mentor info update was successful
            mentorService.updateFixedTimeSlots(mentorId, updatedMentor.getTimeSlots());
            mentorService.updateCertificates(mentorId, updatedMentor.getCertificates());
            mentorService.updateExperience(mentorId, updatedMentor.getExperiences());
            mentorService.updateCategories(mentorId, updatedMentor.getCategories());

            return "Mentor updated successfully!";
        } else {
            return "Mentor update failed!";
        }
    }
    
    // Delete a mentor
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMentor(@PathVariable Long id) {
        try {
            mentorService.deleteMentor(id);
            return ResponseEntity.ok("Mentor deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting mentor: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public List<Map<String, Object>> searchMentors(@RequestParam String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return List.of();
        }

        List<String> items = Arrays.asList(keyword.split("\\s*,\\s*"));
        List<Map<String, Object>> results = new ArrayList<>();
        for (String item : items) {
            List<Map<String, Object>> dbResults = databaseService.searchMentors(item);
            if (new HashSet<>(results).containsAll(dbResults) && new HashSet<>(dbResults).containsAll(results)) {
                log.info("Repeated the same records");
            } else {
                results.addAll(dbResults);
            }
        }
        return results;
    }

    @GetMapping("/verified")
    public List<Map<String, Object>> getVerifiedMentors() {
        return databaseService.getVerifiedMentors();
    }

    @GetMapping("/categories")
    public List<Map<String, String>> getCategories() {
        return databaseService.getAllCategories();
    }

    @GetMapping("/top-rated")
    public List<Map<String, Object>> getTopRatedMentors() {
        return databaseService.getTopRatedMentors();
    }

    @GetMapping("/top-mentor")
    public List<Map<String, Object>> getTopMentors() {
        return databaseService.getTopMentors();
    }

    @GetMapping("/teaching-schedules")
    public List<Map<String, Object>> getTeachingSchedules() {
        return databaseService.getTeachingSchedules();
    }

    @GetMapping("/schedules/{mentorId}")
    public ResponseEntity<List<Map<String, Object>>> getTeachingSchedulesByMentor(@PathVariable Long mentorId) {
        try {
            List<Map<String, Object>> schedules = mentorService.getTeachingSchedulesByMentor(mentorId);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/connect-methods")
    public List<Map<String, Object>> getConnectMethods() {
        return databaseService.getConnectMethods();
    }

    // Get categories for a specific mentor
    @GetMapping("/categories/{id}")
    public List<Map<String, Object>> getMentorCategories(@PathVariable Long id) {
        return databaseService.getMentorCategories(id);
    }

    // Get experiences for a specific mentor
    @GetMapping("/experiences/{id}")
    public List<Map<String, Object>> getMentorExperiences(@PathVariable Long id) {
        return databaseService.getMentorExperiences(id);
    }

    // Get reviews for a specific mentor
    @GetMapping("/reviews{id}")
    public List<Map<String, Object>> getMentorReviews(@PathVariable Long id) {
        return databaseService.getMentorReviews(id);
    }

    // Get certificates for a specific mentor
    @GetMapping("/certificates/{id}")
    public List<Map<String, Object>> getMentorCertificates(@PathVariable Long id) {
        return databaseService.getMentorCertificates(id);
    }

    // Get all data for a specific mentor (mentor details, categories, experiences, reviews, certificates)
    @GetMapping("/{id}")
    public Map<String, Object> getMentorById(@PathVariable Long id) {
        return databaseService.getAllDataForMentor(id);
    }
}

