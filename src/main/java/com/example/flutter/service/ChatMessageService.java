package com.example.flutter.service;

import com.example.flutter.entities.ChatMessage;
import com.example.flutter.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Long senderId, Long recipientId) {
        return chatMessageRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    }

    public List<ChatMessage> getUnreadMessages(Long senderId, Long recipientId) {
        return chatMessageRepository.findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false);
    }

    public void markMessagesAsRead(Long senderId, Long recipientId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.
                findBySenderIdAndRecipientIdAndRead(senderId, recipientId, false);
        unreadMessages.forEach(msg -> msg.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }
}

