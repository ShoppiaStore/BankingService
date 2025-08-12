package com.progect.BankingApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "transactions")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    private double amount;

    private String notes;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    private double balanceAfter;


    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
