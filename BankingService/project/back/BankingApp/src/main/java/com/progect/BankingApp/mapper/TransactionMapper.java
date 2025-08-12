package com.progect.BankingApp.mapper;

import com.progect.BankingApp.dto.TransactionDto;
import com.progect.BankingApp.entity.Transaction;

public class TransactionMapper {
    public static TransactionDto mapToTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getType().toString(),
                transaction.getAmount(),
                transaction.getNotes(),
                transaction.getTransactionDate(),
                transaction.getBalanceAfter()
        );
    }
}
