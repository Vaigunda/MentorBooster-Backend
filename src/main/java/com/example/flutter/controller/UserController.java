package com.example.flutter.controller;

import com.example.flutter.entities.Users;
import com.example.flutter.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserController {

    @Autowired
    UserDetailsServiceImpl userService;

    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@Valid @RequestBody Users user) {
        try {
            return new ResponseEntity<>(
                    this.userService.save(user),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            log.debug("Exception is occurred while create user ");
            throw ex;
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Users> getUserProfile(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(
                    this.userService.findById(userId),
                    HttpStatus.OK);
        } catch (Exception ex) {
            log.debug("Exception is occurred while Get user ");
            throw ex;
        }
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody Users updatedUser) {
        try {
            // Check if the user exists in the database
            Users existingUser = userService.findById(id);
            if (existingUser == null) {
                log.warn("Update failed: User with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Update fields as needed
            existingUser.setName(updatedUser.getName());
            existingUser.setEmailId(updatedUser.getEmailId());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setGender(updatedUser.getGender());

            // Save the updated user to the database
            userService.save(existingUser);
            log.info("User with ID {} updated successfully", id);

            return ResponseEntity.ok("User details updated successfully");
        } catch (Exception ex) {
            log.error("Exception occurred while updating user details: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user details");
        }
    }
}
