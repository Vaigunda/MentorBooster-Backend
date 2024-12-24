package com.example.flutter.service;

import com.example.flutter.dto.CreateNotificationDto;
import com.example.flutter.entities.Notification;
import com.example.flutter.repositories.NotificationRepository;
import com.example.flutter.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<String> createNotification(CreateNotificationDto createNotificationDto) {
        String responseMessage = "";
        try {
            String recipientName = userRepository.findRecipientNameByRecipientId(createNotificationDto.getRecipientId());
            if (recipientName == null) {
                logger.warn("Recipient not found for recipientId: {}", createNotificationDto.getRecipientId());
                responseMessage = "Recipient not found";
            } else {
                String title = createNotificationDto.getTitle();
                String message = createNotificationDto.getMessage();

                Notification notification = Notification.builder()
                        .mentorId(createNotificationDto.getMentorId())
                        .recipientId(createNotificationDto.getRecipientId())
                        .recipientName(recipientName)
                        .title(title)
                        .message(message)
                        .build();
                notificationRepository.save(notification);

                logger.info("Notification successfully created for recipientId: {}", createNotificationDto.getRecipientId());
                responseMessage = "Notification saved successfully";
            }
        } catch (Exception e) {
            logger.error("Failed to create notification for recipientId: {}", createNotificationDto.getRecipientId(), e);
            responseMessage = "Failed to create notification: " + e.getMessage();
        }
        return ResponseEntity.ok(responseMessage);
    }

    public ResponseEntity<List<Notification>> getAllNotificationByMentorId(Long mentorId) {
        List<Notification> notifications = null;
        try {
            notifications = notificationRepository.findByMentorId(mentorId);
            if (notifications.isEmpty()) {
                logger.info("No notifications found for mentorId: {}", mentorId);
            } else {
                logger.info("Found {} notifications for mentorId: {}", notifications.size(), mentorId);
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching notifications for mentorId: {}", mentorId, e.getMessage());
        }
        return ResponseEntity.ok(notifications);
    }

    public ResponseEntity<Optional<Notification>> getNotificationById(Long id) {
        Optional<Notification> notification = Optional.empty();
        try {
            notification = notificationRepository.findById(id);
            if (notification.isPresent()) {
                logger.info("Found notification for notificationId: {}", id);
            } else {
                logger.warn("No notification found for notificationId: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching notification by id: {}", id, e.getMessage());
        }
        return ResponseEntity.ok(notification);
    }


    public ResponseEntity<String> updateAsRead(Long notificationId) {
        String responseMessage = "";
        try {
            Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
            if (optionalNotification.isEmpty()) {
                logger.warn("Notification not found for notificationId: {}", notificationId);
                responseMessage = "Notification not found";
            } else {
                Notification notification = optionalNotification.get();
                notification.setReadAt(LocalDateTime.now());
                notification.setIsRead(true);
                notificationRepository.save(notification);

                logger.info("Notification marked as read for notificationId: {}", notificationId);
                responseMessage = "Notification updated successfully";
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating notification to read for notificationId: {}", notificationId, e);
            responseMessage = "Failed to update notification: " + e.getMessage();
        }
        return ResponseEntity.ok(responseMessage);
    }

}