package com.example.flutter.repositories;

import com.example.flutter.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatMessage> findBySenderIdAndRecipientIdAndRead(Long senderId, Long recipientId, boolean read);
}

