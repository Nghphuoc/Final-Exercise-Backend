package jpa.studentmanagementsystem.service;

import jakarta.mail.MessagingException;
import jpa.studentmanagementsystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public interface EmailService {
    String sendMail(User user);
    void sendHtmlMail(User user) throws MessagingException;
}
