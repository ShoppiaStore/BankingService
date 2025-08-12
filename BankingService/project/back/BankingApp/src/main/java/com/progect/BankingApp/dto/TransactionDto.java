package com.progect.BankingApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private Long accountId;
    private String type; // DEPOSIT or WITHDRAW
    private double amount;
    private String notes;
    private LocalDateTime transactionDate;
    private double balanceAfter;
}


