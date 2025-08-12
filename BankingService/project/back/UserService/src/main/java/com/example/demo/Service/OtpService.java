package com.example.demo.Service;

import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    public Otp generateOtp(User user) {
        //generate random otp
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        Otp otp = new Otp();
        otp.setOtp(otpCode);
        otp.setExpirationTime(expiration);
        otp.setUser(user);
        System.out.println("user id: " + user.getId());



        return otp;
    }




}
