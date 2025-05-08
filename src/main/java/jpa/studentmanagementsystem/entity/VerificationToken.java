package jpa.studentmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

//@Entity
public class VerificationToken {
//    @Id
//    @GeneratedValue
    private Long id;

    private String token;

//    @OneToOne
    private User user;

    private LocalDateTime expiryDate;
}
