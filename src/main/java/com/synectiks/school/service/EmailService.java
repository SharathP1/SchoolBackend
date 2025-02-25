package com.synectiks.school.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCredentialsEmail(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Parent Portal Login Credentials");
        message.setTo(
            "Dear Parent,\n\n" +
            "Your login credentials for the Parent Portal are:\n" +
            "Email: " + toEmail + "\n" +
            "Password: " + password + "\n\n" +
            "Please log in at https://synefo.synectiks.com \n\n" +
            "Best regards,\nSchool Administration"
        );

        mailSender.send(message);
    }
}        