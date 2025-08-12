package com.progect.BankingApp.service;

import com.progect.BankingApp.dto.TransactionDto;
import com.progect.BankingApp.dto.TransactionRequest;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface TransactionService {
    public TransactionDto deposit( String cardNumber, double amount);

    TransactionDto withdraw(String cardNumber, double amount);

    List<TransactionDto> getTransactionHistory(Long accountId);
}
