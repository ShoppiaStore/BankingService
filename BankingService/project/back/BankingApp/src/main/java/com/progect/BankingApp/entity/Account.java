package com.progect.BankingApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "account")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 16, max = 16, message = "card number must be 16 characters long")
    @Column(name = "card_number")
    private String cardNumber;

    @NotBlank(message = "Please provide account name")
    private String accountName;

    @PositiveOrZero(message = "balance must be positive or zero")
    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;


    @Column(name = "user_id")
    private int userId;


    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

}

