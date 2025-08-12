package com.example.demo.Service;

import com.example.demo.entity.User;
import org.springframework.web.bind.annotation.RequestHeader;


public interface UserService {

    public void addUser(User user);

   // public void deleteUser(int id);

    public void updateUser(User user);

    public User searchUser(String email);

    public String forgetPassword (String token);

    public String changePassword(String Authorization,String otp,User user);

    public boolean activateUser(String email , String otp);

    public String regenerateOtp(String email);


    public void deleteUser(User user);

    public String validateToken (String Authorization);

    public int extractUserId (String Authorization);

}
