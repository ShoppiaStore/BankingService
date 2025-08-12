package com.example.demo.Repository;

import com.example.demo.entity.Otp;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepo extends JpaRepository<Otp, Integer> {
    Optional<Otp> findTopByUserOrderByExpirationTimeDesc(User user);
    Optional<Otp> findTopByUserEmailOrderByExpirationTimeDesc(String email);



}
