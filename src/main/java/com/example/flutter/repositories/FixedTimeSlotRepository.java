package com.example.flutter.repositories;

import com.example.flutter.entities.FixedTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedTimeSlotRepository extends JpaRepository<FixedTimeSlot, Long> {

    List<FixedTimeSlot> findByMentor_Id(Long mentorId);

}

