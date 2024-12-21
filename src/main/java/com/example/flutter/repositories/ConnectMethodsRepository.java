package com.example.flutter.repositories;

import com.example.flutter.entities.ConnectMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectMethodsRepository extends JpaRepository<ConnectMethods, String> {
    // This repository automatically has methods like findAll, save, delete, etc.
}
