package com.synectiks.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.synectiks.school.entity.ChatMessage;
import com.synectiks.school.service.ChatService;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public String chat() {
        logger.info("Chat endpoint hit");
        return "chat";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        logger.info("Message sent: {}", chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("User added: {}", chatMessage);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("role", chatMessage.getRole());
        return chatMessage;
    }
}


//
//    @PostMapping("/sendMessage/{chatId}")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> sendMessage(@PathVariable String chatId, @RequestBody Message message) {
//        try {
//            chatService.sendMessage(chatId, message);
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Message sent successfully!");
//            return ResponseEntity.ok(response);
//        } catch (ExecutionException | InterruptedException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "error");
//            response.put("message", "Failed to send message.");
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//
//    @MessageMapping("/chat/{chatId}")
//    @SendTo("/topic/messages/{chatId}")
//    public Message broadcastMessage(@PathVariable String chatId, @RequestBody Message message) {
//        return message;
//    }
//
//    @GetMapping("/chatHistory/{chatId}")
//    @ResponseBody
//    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String chatId) {
//        try {
//            List<Message> chatHistory = chatService.getChatHistory(chatId);
//            return ResponseEntity.ok(chatHistory);
//        } catch (ExecutionException | InterruptedException e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }
//
//    @PostMapping("/createChatSession/{teacherId}")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> createChatSession(@PathVariable String teacherId) {
//        try {
//            String chatId = chatService.createChatSessionForTeacher(teacherId);
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("chatId", chatId);
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "error");
//            response.put("message", "Invalid teacher ID.");
//            return ResponseEntity.status(400).body(response);
//        } catch (ExecutionException | InterruptedException e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "error");
//            response.put("message", "Failed to create chat session.");
//            return ResponseEntity.status(500).body(response);
//        }
//    }

