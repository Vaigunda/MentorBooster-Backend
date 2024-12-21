package com.example.flutter.controller;

import com.example.flutter.dto.CategoryDTO;
import com.example.flutter.dto.MentorDTO;
import com.example.flutter.dto.MentorResponseDTO;
import com.example.flutter.dto.MentorSearchDTO;
import com.example.flutter.service.HomePageService;
import com.example.flutter.entities.*;
import com.example.flutter.service.UsersService;
import com.example.flutter.util.CommonFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
//import com.example.flutter.service.DatabaseService;

import com.example.flutter.service.MentorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RestController
@RequestMapping("/api/mentors")
public class MentorController {

//    @Autowired
//    private DatabaseService databaseService;

    @Autowired
    private MentorService mentorService;

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private UsersService usersService;

    @Autowired
    CommonFiles commonFiles;

    // with repo
    @GetMapping("/get/{id}")
    public MentorDTO getMentor(@PathVariable Long id) {
        return mentorService.getMentorById(id);
    }

    @GetMapping("/get")
    public List<MentorDTO> getAllMentor() {
        return mentorService.getAllMentor(); // Call service method to get all mentors
    }

    @GetMapping("/get-no-time-slots")
    public List<MentorDTO> getAlMentor() {
        return mentorService.getAlMentor(); // Call service method to get all mentors
    }

    //Delete a mentor with repo
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMentors(@PathVariable Long id) {
        try {
            mentorService.deleteMentors(id);
            return ResponseEntity.ok("Mentor deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting mentor: " + e.getMessage());
        }
    }

    // Update with repo
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateMentors(@PathVariable Long id, @RequestBody Mentor mentorDetails) {
        System.out.println("RECEIVED"+mentorDetails);
        String response = mentorService.updateMentor(id, mentorDetails);
        return ResponseEntity.ok(response);
    }

    // add mentor with repo ( here in request body in categories we need id but we get name so we can't directly save
    @PostMapping("/add")
    public ResponseEntity<String> saveMentor(@RequestBody Mentor mentor) {
        try {

            // Check if the mentor's email already exists
            if (mentorService.existsByEmail(mentor.getEmail())) {
                return ResponseEntity.ok("Email already Exists");
            }

            // Call the service to add the mentor
            String responseMessage = mentorService.saveMentor(mentor);

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

    // search with repo
    @GetMapping("/mentors-search")
    public List<MentorSearchDTO> searchMentor(@RequestParam String keyword) {
        return mentorService.searchMentors(keyword);
    }

    @GetMapping("/verified-mentors")
    public List<MentorResponseDTO> getVerifiedMentor() {
        return homePageService.getVerifiedMentors();
    }

    @GetMapping("/mentors-top-rated")
    public List<MentorResponseDTO> getTopRatedMentor() {
        return homePageService.getTopRatedMentors();
    }

    @GetMapping("/mentors-top")
    public List<MentorResponseDTO> getTopMentor() {
        return homePageService.getTopMentors();
    }

    @GetMapping("/connect-methods-mentors")
    public List<ConnectMethods> getAllConnectMethods() {
        return homePageService.getAllConnectMethods();
    }

    @GetMapping("/categories-mentors")
    public List<CategoryDTO> getAllCategories() {
        return homePageService.getAllCategories();
    }







}

//    // Get all mentors including their time slots
//    @GetMapping("/all")
//    public List<Map<String, Object>> getAllMentors() {
//        return mentorService.getAllMentors();
//    }
//
//
//    @GetMapping
//    public  List<Map<String, Object>> getAllMentorsInfo() {
//        return databaseService.getAllMentorsInfo();
//    }
//
//    @PostMapping
//    public ResponseEntity<String> createMentor(@RequestBody Mentor mentor) {
//        try {
//
//            // Check if the mentor's email already exists
//            if (mentorService.existsByEmail(mentor.getEmail())) {
//                return ResponseEntity.ok("Email already Exists");
//            }
//
//            // Call the service to add the mentor
//            String responseMessage = mentorService.addMentor(mentor);
//
//            if (responseMessage.equals("Mentor added successfully!")) {
//                // Instantiate a PasswordEncoder (you could also inject this via @Autowired)
//                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//                String password = commonFiles.generateAlphaPassword(6);
//                String hashedPassword = passwordEncoder.encode(password);
//
//                //Create User after Mentor create
//                Users users = new Users();
//                users.setName(mentor.getName());
//                users.setEmailId(mentor.getEmail());
//                users.setUserName(commonFiles.generateAlphaPassword(6));
//                users.setUserType("Mentor");
//                users.setPassword(hashedPassword);
//
//                Users newMentor = usersService.save(users);
//                if (newMentor != null) {
//                    //Send Email for Mentor
//                    commonFiles.sendPasswordToMentor(mentor, password);
//                }
//            }
//            // Return HTTP 201 with the success message
//            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
//        } catch (Exception e) {
//            // Log the error (optional but recommended)
//            e.printStackTrace();
//            // Return a bad request response in case of error
//            return new ResponseEntity<>("Failed to add mentor. Please check the input data.", HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // Update Mentor Info
//    @PutMapping("/{mentorId}")
//    public String updateMentor(@PathVariable Long mentorId, @RequestBody Mentor updatedMentor) {
//        // Print the mentorId
//
//        int updateCount = mentorService.updateMentorInfo(
//                mentorId, updatedMentor.getName(), updatedMentor.getEmail(), updatedMentor.getAvatarUrl(),
//                updatedMentor.getBio(), updatedMentor.getRole(), updatedMentor.getFreePrice(),
//                updatedMentor.getFreeUnit(), updatedMentor.getVerified(), updatedMentor.getRate(),
//                updatedMentor.getNumberOfMentoree()
//        );
//
//        if (updateCount == 1) {
//            // Print additional logs
//
//            mentorService.updateFixedTimeSlots(mentorId, updatedMentor.getTimeSlots());
//
//
//            mentorService.updateCertificates(mentorId, updatedMentor.getCertificates());
//
//
//            mentorService.updateExperience(mentorId, updatedMentor.getExperiences());
//
//
//            mentorService.updateCategories(mentorId, updatedMentor.getCategories());
//
//
//            return "Mentor updated successfully!";
//        } else {
//            System.out.println("Mentor update failed!");
//            return "Mentor update failed!";
//        }
//    }
//
//    // Update an existing mentor
//
//
//    // Delete a mentor
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteMentor(@PathVariable Long id) {
//        try {
//            mentorService.deleteMentor(id);
//            return ResponseEntity.ok("Mentor deleted successfully.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error deleting mentor: " + e.getMessage());
//        }
//    }
//
//
//
//    @GetMapping("/search")
//    public List<Map<String, Object>> searchMentors(@RequestParam String keyword) {
//        return databaseService.searchMentors(keyword);
//    }
//
//    // New API: Get all verified mentors
////    @CrossOrigin(origins = {"http://localhost:51524", "http://localhost:62816"})
//    @GetMapping("/verified")
//    public List<Map<String, Object>> getVerifiedMentors() {
//        return databaseService.getVerifiedMentors();
//    }
//
////    @CrossOrigin(origins = {"http://localhost:51524", "http://localhost:62816"})
//    @GetMapping("/categories")
//    public List<Map<String, String>> getCategories() {
//        return databaseService.getAllCategories();
//    }
//
//    // New API: Get all top-rated mentors (sorted by rating in descending order)
////    @CrossOrigin(origins = {"http://localhost:51524", "http://localhost:62816"})
//    @GetMapping("/top-rated")
//    public List<Map<String, Object>> getTopRatedMentors() {
//        return databaseService.getTopRatedMentors();
//    }
//
////    @CrossOrigin(origins = {"http://localhost:51524", "http://localhost:62816"})
//    @GetMapping("/top-mentor")
//    public List<Map<String, Object>> getTopMentors() {
//        return databaseService.getTopMentors();
//    }
//
//    @GetMapping("/teaching-schedules")
//    public List<Map<String, Object>> getTeachingSchedules() {
//        return databaseService.getTeachingSchedules();
//    }
//
//    @GetMapping("/schedules/{mentorId}")
//    public ResponseEntity<List<Map<String, Object>>> getTeachingSchedulesByMentor(@PathVariable Long mentorId) {
//        try {
//            List<Map<String, Object>> schedules = mentorService.getTeachingSchedulesByMentor(mentorId);
//            return ResponseEntity.ok(schedules);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    @GetMapping("/connect-methods")
//    public List<Map<String, Object>> getConnectMethods() {
//        return databaseService.getConnectMethods();
//    }
//
//
//
//    // Get categories for a specific mentor
//    @GetMapping("/categories/{id}")
//    public List<Map<String, Object>> getMentorCategories(@PathVariable Long id) {
//        return databaseService.getMentorCategories(id);
//    }
//
//    // Get experiences for a specific mentor
//    @GetMapping("/experiences/{id}")
//    public List<Map<String, Object>> getMentorExperiences(@PathVariable Long id) {
//        return databaseService.getMentorExperiences(id);
//    }
//
//    // Get reviews for a specific mentor
//    @GetMapping("/reviews{id}")
//    public List<Map<String, Object>> getMentorReviews(@PathVariable Long id) {
//        return databaseService.getMentorReviews(id);
//    }
//
//    // Get certificates for a specific mentor
//    @GetMapping("/certificates/{id}")
//    public List<Map<String, Object>> getMentorCertificates(@PathVariable Long id) {
//        return databaseService.getMentorCertificates(id);
//    }
//
//    // Get all data for a specific mentor (mentor details, categories, experiences, reviews, certificates)
//    @GetMapping("/{id}")
//    public Map<String, Object> getMentorById(@PathVariable Long id) {
//        return databaseService.getAllDataForMentor(id);
//    }
//}

