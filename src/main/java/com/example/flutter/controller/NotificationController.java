package com.example.flutter.controller;

import com.example.flutter.dto.CreateNotificationDto;
import com.example.flutter.entities.Notification;
import com.example.flutter.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "${cors.allow-origin}", maxAge = 3600)
@RequestMapping("/api/notify")
@RestController
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService notificationService;

    @PostMapping("/createNotification")
    public ResponseEntity<String> createNotification (@RequestBody CreateNotificationDto createNotificationDto){
        logger.info("Creating notification for recipientId: {} and mentorId: {}", createNotificationDto.getRecipientId(), createNotificationDto.getMentorId());
        return notificationService.createNotification(createNotificationDto);
    }

    @GetMapping("/getAllNotificationByMentorId")
    public ResponseEntity<List<Notification>> getAllNotificationByMentorId(@RequestParam Long mentorId){
        logger.info("Fetching notifications for mentorId: {}", mentorId);
        return notificationService.getAllNotificationByMentorId(mentorId);
    }

    @GetMapping("/getNotificationById")
    public ResponseEntity<Optional<Notification>>getNotificationById(@RequestParam Long id){
        logger.info("Fetching notification by id={}", id);
        return notificationService.getNotificationById(id);
    }

    @PutMapping("/updateAsRead")
    public ResponseEntity<String> updateAsRead(@RequestParam Long notificationId){
        logger.info("Updating notification as read for notificationId: {}", notificationId);
        return notificationService.updateAsRead(notificationId);
    }

}
