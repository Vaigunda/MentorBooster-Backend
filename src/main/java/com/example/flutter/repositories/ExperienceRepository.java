package com.example.flutter.repositories;

import com.example.flutter.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByMentorId(Long mentorId);
}

