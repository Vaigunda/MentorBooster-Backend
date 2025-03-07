package com.example.flutter.repositories;

import com.example.flutter.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    //@Query("SELECT n FROM Notification n WHERE n.mentorId = :mentorId")
    List<Notification> findByMentorIdAndIsRead(Long mentorId, Boolean isRead);

    //@Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId")
    List<Notification> findByRecipientIdAndIsRead(Long recipientId, Boolean isRead);

    List<Notification> findByIsRead(Boolean isRead);
}
