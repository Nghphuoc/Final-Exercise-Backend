package jpa.studentmanagementsystem.controller;

import jakarta.mail.MessagingException;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.exception.ErrorResponse;
import jpa.studentmanagementsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody User user) {
        try {
            emailService.sendMail(user);
            return ResponseEntity.ok(new ErrorResponse("Success","Email send successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Error", e.getMessage()));
        }
    }

    @PostMapping("/send-html")
    public ResponseEntity<?> sendEmailHtml(@RequestBody User user) throws MessagingException {
        try {
            emailService.sendHtmlMail(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ErrorResponse("Success", "Email send successfylly!!!"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponse("Error", e.getMessage()));

        }
    }
}
