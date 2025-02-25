//package com.synectiks.school.service;
//
//import com.google.api.core.ApiFuture;
//import com.google.cloud.firestore.*;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.synectiks.school.model.MessageEntity;
//import com.synectiks.school.repository.MessageRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//@Service
//public class MessageService {
//
//    @Autowired
//    private MessageRepository messageRepository;
//
//    @Autowired
//    private FirebaseMessaging firebaseMessaging;
//
//    private final Firestore firestore;
//
//    public MessageService() {
//        FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
//                .setProjectId("school-7ff19") // Replace with your Firebase project ID
//                .build();
//        this.firestore = firestoreOptions.getService();
//    }
//
//    public MessageEntity sendMessage(String sender, String receiver, String content, String className, String section) throws ExecutionException, InterruptedException {
//        // Validate sender and receiver based on class and section details
//        if (!validateSenderReceiver(sender, receiver, className, section)) {
//            throw new IllegalArgumentException("Invalid sender or receiver details.");
//        }
//
//        MessageEntity messageEntity = new MessageEntity();
//        messageEntity.setSender(sender);
//        messageEntity.setReceiver(receiver);
//        messageEntity.setContent(content);
//        messageEntity.setTimestamp(LocalDateTime.now());
//        messageRepository.save(messageEntity);
//
//        // Send FCM notification
//        Notification notification = Notification.builder()
//                .setTitle(sender)
//                .setBody(content)
//                .build();
//
//        Message fcmMessage = Message.builder()
//                .setNotification(notification)
//                .setToken(receiver) // Receiver's FCM token
//                .build();
//
//        try {
//            firebaseMessaging.send(fcmMessage);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//            // Handle the exception as needed
//        }
//
//        // Store conversation in Firestore
//        storeConversationInFirestore(sender, receiver, content);
//
//        return messageEntity;
//    }
//
//    public List<MessageEntity> getMessages(String receiver) {
//        return messageRepository.findByReceiver(receiver);
//    }
//
//    private void storeConversationInFirestore(String sender, String receiver, String content) {
//        CollectionReference conversations = firestore.collection("Parent-Teacher Conversation");
//
//        Map<String, Object> conversationData = new HashMap<>();
//        conversationData.put("sender", sender);
//        conversationData.put("receiver", receiver);
//        conversationData.put("content", content);
//        conversationData.put("timestamp", LocalDateTime.now().toString());
//
//        ApiFuture<DocumentReference> future = conversations.add(conversationData);
//
//        try {
//            DocumentReference documentReference = future.get();
//            System.out.println("Conversation stored with ID: " + documentReference.getId());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            // Handle the exception as needed
//        }
//    }
//
//    private boolean validateSenderReceiver(String sender, String receiver, String className, String section) throws ExecutionException, InterruptedException {
//        CollectionReference parentsCollection = firestore.collection("Student_Details");
//        CollectionReference teachersCollection = firestore.collection("Teacher_Details");
//
//        // Query parent details
//        Query parentQuery = parentsCollection.whereEqualTo("Class", className)
//                .whereEqualTo("Section", section);
//        ApiFuture<QuerySnapshot> parentQuerySnapshot = parentQuery.get();
//        List<QueryDocumentSnapshot> parentDocuments = parentQuerySnapshot.get().getDocuments();
//
//        if (parentDocuments.isEmpty()) {
//            return false;
//        }
//
//        // Query teacher details
//        Query teacherQuery = teachersCollection.whereEqualTo("className", className);
////                .whereEqualTo("section", section);
//        ApiFuture<QuerySnapshot> teacherQuerySnapshot = teacherQuery.get();
//        List<QueryDocumentSnapshot> teacherDocuments = teacherQuerySnapshot.get().getDocuments();
//
//        if (teacherDocuments.isEmpty()) {
//            return false;
//        }
//
//        // Validate sender and receiver
//        String parentId = parentDocuments.get(0).getString("parentId");
//        String teacherId = teacherDocuments.get(0).getString("teacherId");
//
//        return sender.equals(parentId) && receiver.equals(teacherId);
//    }
//}
