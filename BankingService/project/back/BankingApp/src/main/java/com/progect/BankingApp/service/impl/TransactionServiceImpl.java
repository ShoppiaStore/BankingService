package com.progect.BankingApp.service.impl;

import com.progect.BankingApp.dto.TransactionDto;
import com.progect.BankingApp.entity.Account;
import com.progect.BankingApp.entity.Transaction;
import com.progect.BankingApp.entity.TransactionType;
import com.progect.BankingApp.repositry.AccountRepository;
import com.progect.BankingApp.mapper.TransactionMapper;
import com.progect.BankingApp.repositry.TransactionRepository;
import com.progect.BankingApp.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    @Override
    @Transactional
    public TransactionDto deposit(String cardNumber, double amount) {
        // Find account
        Account account = accountRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Validate amount
        if (amount <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        // Update account balance
        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .account(savedAccount)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .balanceAfter(savedAccount.getBalance())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.mapToTransactionDto(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDto withdraw(String cardNumber, double amount) {
            Account account = accountRepository.findByCardNumber(cardNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            if (amount <= 0) {
                throw new RuntimeException("Amount must be positive");
            }

           if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance");
            }

            account.setBalance(account.getBalance() - amount);
            Account savedAccount = accountRepository.save(account);

            Transaction transaction = Transaction.builder()
                    .account(savedAccount)
                    .type(TransactionType.WITHDRAWA)
                    .amount(amount)
                    .transactionDate(LocalDateTime.now())
                    .balanceAfter(savedAccount.getBalance())
                    .build();

            Transaction savedTransaction = transactionRepository.save(transaction);
            return TransactionMapper.mapToTransactionDto(savedTransaction);
    }

    @Override
    public List<TransactionDto> getTransactionHistory(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
        return transactions.stream()
                .map(TransactionMapper::mapToTransactionDto)
                .collect(Collectors.toList());
    }
}
