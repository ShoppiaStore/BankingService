package com.example.demo.Service;


import com.example.demo.Repository.OtpRepo;
import com.example.demo.Repository.TokenRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.entity.Otp;
import com.example.demo.entity.Token;
import com.example.demo.entity.TokenType;
import com.example.demo.entity.User;
import com.example.demo.model.request.AuthenticationResponse;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private OtpRepo otpRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailServiceImpl emailService;

    public AuthenticationResponse login(LoginRequest request)
    {  System.out.println(" AuthenticationResponse login");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
        System.out.println(" AuthenticationResponse login  2");
        User user = userRepository.findUserByEmail(request.getEmail());
        System.out.println(" AuthenticationResponse login 3");
        Map<String , Object> extraClaims = new HashMap<>();
        System.out.println(" AuthenticationResponse login 4");
        String jwtToken = jwtService.createToken(user , extraClaims);
        System.out.println(" AuthenticationResponse login 5");
        saveUserToken(user, jwtToken);
        System.out.println(" AuthenticationResponse login 6");

        return new AuthenticationResponse(jwtToken , request.getEmail());
    }

    public AuthenticationResponse register(RegisterRequest request)
    {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already taken");
        }

        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalStateException("Name is already taken");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .phone(request.getPhone())
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);
        Map<String , Object> extraClaims = new HashMap<>();
        String jwtToken = jwtService.createToken(user , extraClaims);
        saveUserToken(savedUser, jwtToken);
         // Generate OTP
               Otp otp = otpService.generateOtp(user);
        //        // store otp
                otpRepo.save(otp);
          emailService.sendOtpMsg(user.getEmail(),"Your Verification Code", otp.getOtp());
        return new AuthenticationResponse(jwtToken , request.getEmail());
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .createdAt(LocalDateTime.now()) // هام
                .expirationDate(LocalDateTime.now())
               // .expired(false)
                //.revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
