package com.example.flutter.service;

import com.example.flutter.entities.Users;
import com.example.flutter.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (authenticateUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + users.getEmailId());
        }
        logger.info("User Name : {}", authenticateUser.getUserName());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticateUser.getUserName(),
                            users.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new UsernameNotFoundException("Invalid credentials for email: " + users.getEmailId());
        }

        return authenticateUser;
    }
}
