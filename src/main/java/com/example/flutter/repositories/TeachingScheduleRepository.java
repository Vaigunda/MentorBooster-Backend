package com.example.flutter.repositories;



import com.example.flutter.entities.TeachingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingScheduleRepository extends JpaRepository<TeachingSchedule, Long> {
}

