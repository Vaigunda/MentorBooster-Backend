package com.example.flutter.repositories;



import com.example.flutter.entities.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Mentor findByEmail(String email);

    boolean existsByEmail(String email);
}
