package com.example.demo.Repository;


import com.example.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {


  Token findByToken(String token);
}
