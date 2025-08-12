package com.example.demo.Service;

import com.example.demo.Exceptions.NotFoundException;
import com.example.demo.Repository.OtpRepo;
import com.example.demo.Repository.UserRepository;
import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImp implements UserService{

    @Autowired
     private UserRepository userRepository;
    @Autowired
    private OtpRepo otpRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public void addUser(@Valid User user) {
        userRepository.save(user);
    }

   // @Override
   // public void deleteUser(int id) {
     //   if (!userRepository.existsById(id)) {
         //   throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");}
      //  userRepository.deleteById(id);
  //  }

    @Override
    public void deleteUser(User user) {
        System.out.println("delete");
        int id =user.getId();
        System.out.println(id);
        if (!userRepository.existsById(id)) {
            System.out.println("if");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");}
        userRepository.deleteById(id);
    }

    @Override
    public String validateToken(String Authorization) {
        System.out.println("validate token function");
        String token = Authorization.replace("Bearer ", "");
        System.out.println(token);
        String email = jwtService. extractEmail(token);
        System.out.println(email);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("if in userservice");
            return "invalid token";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        boolean isValid= jwtService.isTokenValid(token,userDetails);
        System.out.println(isValid);
        return isValid ? "valid token" :"invalid token";

    }

    @Override
    public int extractUserId(String Authorization) {
        String token = Authorization.replace("Bearer ", "");

       return jwtService.extractUserId(token);
    }


    @Override
    public void updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());

        if (existingUser.isPresent()) {
            User user2 = existingUser.get();  // extract item
            user2.setEmail(user.getEmail());
            user2.setPassword(passwordEncoder.encode(user.getPassword()));
            user2.setEnabled(user.isEnabled());

          userRepository.save(user2);
        } else {
            throw new NotFoundException("Item not found with id: " + user.getId());
        }

    }

   @Override
    public User searchUser(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user = new User();
        if (existingUser.isPresent()) {
            user= existingUser.get();  // extract item

        } else {
            throw new NotFoundException("User not found with email: " + email);
        }
        return user;


    }

    @Override
    public String forgetPassword(String Authorization) {
        Authorization = Authorization.replace("Bearer ", "");

        String email = jwtService.extractEmail(Authorization);

        User user=userRepository.findUserByEmail(email);

         // Generate OTP
        Otp otp = otpService.generateOtp(user);
        // store otp
        otpRepo.save(otp);
      //  emailService.sendOtpMsg(user.getEmail(),"Your Verification Code", otp);
        return "otp saved";

    }

    @Override
    public String changePassword(String Authorization, String otp1, User user1) {
        //extract email from token
        Authorization = Authorization.replace("Bearer ", "");
        String email = jwtService.extractEmail(Authorization);
        User user=userRepository.findUserByEmail(email);

        //check otp
        Optional<Otp> otp =otpRepo.findTopByUserOrderByExpirationTimeDesc(user);
        if (otp.isPresent()) {
            String otpCode = otp.get().getOtp();
            System.out.println(otpCode);
            System.out.println(otp1);
            if(Objects.equals(otpCode, otp1))
            {
                System.out.println("inside");
                user.setPassword(passwordEncoder.encode(user1.getPassword()));
                userRepository.save(user);
                return "true inside if";
            }
        }

        else {
            // مفيش OTP مرتبط باليوزر ده
            System.out.println("faild");
            return  "inside else";
        }
        return "out if else";






    }

    @Override
    public boolean activateUser(String email, String otp1) {
        System.out.println(otp1);
        Optional<Otp> otp = otpRepo.findTopByUserEmailOrderByExpirationTimeDesc(email);
        if (otp.isPresent()) {
            String otpCode = otp.get().getOtp();
            System.out.println(otpCode + otp1);
            if(Objects.equals(otpCode, otp1))
            {
                System.out.println("inside nested if ");
                User user = userRepository.findUserByEmail(email);
                user.setEnabled(true);
                userRepository.save(user);
                return true;
            }

        }
        return false;

    }

    @Override
    public String regenerateOtp(String email) {
        User user=userRepository.findUserByEmail(email);
        System.out.println(user);

         if(user!=null) {
             // Generate OTP
             Otp otp = otpService.generateOtp(user);
             // store otp
             otpRepo.save(otp);
              emailService.sendOtpMsg(user.getEmail(),"Your Verification Code", otp.getOtp());
             return "otp saved";
         }
         else {
             return" user not found ";
         }


    }
}
