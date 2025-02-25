package com.synectiks.school.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.synectiks.school.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Firestore firestore;

    public ChatService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    public String createChatSession() throws ExecutionException, InterruptedException {
        String chatId = UUID.randomUUID().toString();
        DocumentReference chatDocRef = firestore.collection("chatSessions").document(chatId);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("chatId", chatId);
        chatData.put("messages", new ArrayList<Map<String, Object>>());
        ApiFuture<WriteResult> result = chatDocRef.set(chatData);
        result.get(); // Wait for the write to complete
        return chatId;
    }

    public String createChatSessionForTeacher(String teacherId) throws ExecutionException, InterruptedException {
        if (!isTeacherIdValid(teacherId)) {
            throw new IllegalArgumentException("Invalid teacher ID");
        }
        String chatId = UUID.randomUUID().toString();
        DocumentReference chatDocRef = firestore.collection("chatSessions").document(chatId);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("chatId", chatId);
        chatData.put("teacherId", teacherId);
        chatData.put("messages", new ArrayList<Map<String, Object>>());
        ApiFuture<WriteResult> result = chatDocRef.set(chatData);
        result.get(); // Wait for the write to complete
        return chatId;
    }

    public void sendMessage(String chatId, Message message) throws ExecutionException, InterruptedException {
        DocumentReference chatDocRef = firestore.collection("chatSessions").document(chatId);
        ApiFuture<DocumentSnapshot> future = chatDocRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) document.get("messages");
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("sender", message.getSender());
            messageData.put("content", message.getContent());
            messageData.put("timestamp", new Date());
            messages.add(messageData);
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("messages", messages);
            chatDocRef.update(updateData);
            messagingTemplate.convertAndSend("/topic/messages/" + chatId, message);
        }
    }

    public List<Message> getChatHistory(String chatId) throws ExecutionException, InterruptedException {
        DocumentReference chatDocRef = firestore.collection("chatSessions").document(chatId);
        ApiFuture<DocumentSnapshot> future = chatDocRef.get();
        DocumentSnapshot document = future.get();
        List<Message> chatHistory = new ArrayList<>();
        if (document.exists()) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) document.get("messages");
            for (Map<String, Object> messageData : messages) {
                Message message = new Message();
                message.setSender((String) messageData.get("sender"));
                message.setContent((String) messageData.get("content"));
                com.google.cloud.Timestamp timestamp = (com.google.cloud.Timestamp) messageData.get("timestamp");
                message.setTimestamp(timestamp.toDate());
                chatHistory.add(message);
            }
        }
        return chatHistory;
    }


    public String getChatIdForTeacher(String teacherId) throws ExecutionException, InterruptedException {
        CollectionReference chatCollection = firestore.collection("chatSessions");
        Query query = chatCollection.whereEqualTo("teacherId", teacherId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            if (document.exists()) {
                return document.getString("chatId");
            }
        }
        return null;
    }

    private boolean isTeacherIdValid(String id) throws ExecutionException, InterruptedException {
        CollectionReference teacherCollection = firestore.collection("Teacher_Details");
        Query query = teacherCollection.whereEqualTo("id", id);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            if (document.exists()) {
                return true;
            }
        }
        return false;
    }
}
