package com.progect.BankingApp.service.impl;

import com.progect.BankingApp.Exception.ResourceNotFoundException;
import com.progect.BankingApp.dto.AccountDto;
import com.progect.BankingApp.entity.Account;
import com.progect.BankingApp.mapper.AccountMapper;
import com.progect.BankingApp.repositry.AccountRepository;
import com.progect.BankingApp.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public AccountDto createAccount(AccountDto accountDto, int userIdFromToken) {

        // Validation
        if (userIdFromToken <= 0) {
            throw new IllegalArgumentException("Invalid user ID extracted from token");
        }

        System.out.println("Creating account for user ID: " + userIdFromToken);
        Account account = AccountMapper.mapToAccountWithUserId(accountDto, userIdFromToken);
        account.setCardNumber(generateCardNumber());
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        if (accountRepository.existsByCardNumber(accountDto.getCardNumber())) {
            throw new IllegalArgumentException("Account with this card number already exists");
        }

        existingAccount.setAccountName(accountDto.getAccountName());
        existingAccount.setAccountType(accountDto.getAccountType());

        Account updatedAccount = accountRepository.save(existingAccount);
        return AccountMapper.mapToAccountDto(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (account.getBalance() > 0) {
            throw new IllegalStateException("Cannot delete account with non-zero balance");
        }
        accountRepository.delete(account);
    }

    @Override
    public List<AccountDto> getAccountsByUserId(int userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        System.out.println("عدد الحسابات: " + accounts.size());
        return accounts.stream()
                .map(AccountMapper::mapToAccountDto)
                .toList();
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();

        // نبدأ برقم البنك (مثلاً 4532)
        cardNumber.append("4532");

        // باقي 12 رقم عشوائي
        for (int i = 0; i < 12; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

}