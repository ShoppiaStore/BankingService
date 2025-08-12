package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender emailSender;

    public void sendOtpMsg(String email, String subject, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("taghreed1225@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText("your code is : "+otp);
        emailSender.send(message);
        System.out.println("Mail sent");
    }
}
