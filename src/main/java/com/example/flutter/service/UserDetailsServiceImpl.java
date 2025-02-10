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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public Users getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Users save(Users user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                new ArrayList<>());
    }

    public Users findById(Long id) {
        return userRepository.findById(id).get();
    }

    public boolean existsByEmail(String emailId) {
        return userRepository.existsByEmailId(emailId);
    }

    public boolean existsByUser(String username) {
        return userRepository.existsByUserName(username);
    }


}
