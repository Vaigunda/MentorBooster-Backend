package com.example.flutter.service;

import com.example.flutter.entities.Users;
import com.example.flutter.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    // Create a logger instance
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    public Users authenticate(Users users) {
        logger.info("Email Id : {}", users.getEmailId());
        Users authenticateUser = userRepository.findByEmailId(users.getEmailId());

        logger.info("User Name : {}", authenticateUser.getUserName());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateUser.getUserName(),
                        users.getPassword()
                )
        );

        return authenticateUser;
    }
}
