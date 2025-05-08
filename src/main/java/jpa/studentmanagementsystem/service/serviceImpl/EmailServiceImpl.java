package jpa.studentmanagementsystem.service.serviceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jpa.studentmanagementsystem.entity.User;
import jpa.studentmanagementsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public String sendMail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom("huuphuocit04@gmail.com");
            message.setSubject("Email confirm form PTPT | AIT! ");
            message.setText("this is email test!!");
            mailSender.send(message);
            return "Email sent successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendHtmlMail(User user) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("huuphuocit04@gmail.com");
        mimeMessageHelper.setTo("phuocvk9000@gmail.com");
        mimeMessageHelper.setSubject("Thanks for applying in Backend Developer Intern (Java)");
        mimeMessageHelper.setText("Dear Nguyen Huu Phuoc,\n" +
                "Thank you for your interest in the Backend Developer Intern (NodeJS/Java) position (Backend Developer Intern (NodeJS/Java)) at Phu Hung Securities (PHS). We truly appreciate the time and effort you invested in learning about the role and submitting your application.\n" +
                "Your qualifications and experience will be carefully reviewed, and we will contact you if there is a suitable match. Due to the high volume of applications, Phu Hung Securities may only reach out to candidates who best meet the requirements of the position. In the meantime, please feel free to explore other career opportunities on our Careers page and follow us on LinkedIn.\n" +
                "We wish you continued success in your career and hope to have the opportunity to connect with you in the future.\n" +
                "Warm Regards,\n" +
                "Phu Hung Securities (PHS)\n" +
                "Talent Acquisition Department - HR Division");
        mimeMessageHelper.setText("http://localhost:4200/login");
        //mimeMessageHelper.addAttachment("logo.cc", new File("logo.cc"));
        mailSender.send(mimeMessage);
    }
}
