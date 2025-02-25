//package com.synectiks.school.controller;
//
//import com.synectiks.school.model.MessageEntity;
//import com.synectiks.school.service.MessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//@RestController
//@RequestMapping("/messages")
//public class MessageController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @PostMapping("/send")
//    public MessageEntity sendMessage(
//            @RequestParam String sender,
//            @RequestParam String receiver,
//            @RequestParam String content,
//            @RequestParam String className,
//            @RequestParam String section) throws ExecutionException, InterruptedException {
//        return messageService.sendMessage(sender, receiver, content, className, section);
//    }
//
//    @GetMapping("/receive")
//    public List<MessageEntity> getMessages(@RequestParam String receiver) {
//        return messageService.getMessages(receiver);
//    }
//}
