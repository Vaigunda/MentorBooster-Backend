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

    @PostMapping("/login")
    public ResponseEntity<Users> loginUser(@Valid @RequestBody Users user) {
        System.out.println("inside login");
        try {
            return new ResponseEntity<>(
                    this.userService.getUserByUserName(user.getUserName()),
                    HttpStatus.OK);
        } catch (Exception ex) {
            log.debug("Exception is occurred while login the user ");
            throw ex;
        }
    }
}
