package com.example.flutter.service;

import com.example.flutter.entities.Users;
import com.example.flutter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    public Users authenticate(Users users) {
        Users authenticateUser = userRepository.findByEmailId(users.getEmailId());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateUser.getUserName(),
                        users.getPassword()
                )
        );

        return authenticateUser;
    }
}
