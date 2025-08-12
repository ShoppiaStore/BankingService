package com.example.demo.entity;
import jakarta.validation.constraints.NotBlank;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp")

public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String otp;

    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
