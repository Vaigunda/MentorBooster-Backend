package com.example.flutter.service;

import com.example.flutter.entities.Users;
import com.example.flutter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsersService {

    @Autowired
    UserRepository userRepository;

    public Users getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Users save(Users user) {
        return userRepository.save(user);
    }

    public Users findById(Long id) {
        return userRepository.findById(id).get();
    }
}
