package com.example.flutter.controller;

import com.example.flutter.entities.ChatMessage;
import com.example.flutter.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    ChatMessageService chatMessageService;

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatMessageService.saveMessage(message);
    }

    @GetMapping("/history/{senderId}/{recipientId}")
    public List<ChatMessage> getChatHistory(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return chatMessageService.getChatHistory(senderId, recipientId);
    }

    @GetMapping("/unread/{senderId}/{recipientId}")
    public List<ChatMessage> getUnreadMessages(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return chatMessageService.getUnreadMessages(senderId, recipientId);
    }

    @GetMapping("/markAsRead/{senderId}/{recipientId}")
    public String markMessagesAsRead(@PathVariable Long senderId, @PathVariable Long recipientId) {
        chatMessageService.markMessagesAsRead(senderId, recipientId);
        return "Messages marked as read";
    }
}

