package com.example.demo.entity;


import jakarta.validation.constraints.NotBlank;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
//@Data  // equal to @Setter , @Getter , @ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jwt")


public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String token;

    private LocalDateTime createdAt=LocalDateTime.now();;
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Token.java
    @Override
    public String toString() {
        return "Token{id=" + id + ", userId=" + (user != null ? user.getId() : null) + "}";
    }

}
