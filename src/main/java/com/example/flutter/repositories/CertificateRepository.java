package com.example.flutter.repositories;

import com.example.flutter.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByMentorId(Long mentorId);
}

