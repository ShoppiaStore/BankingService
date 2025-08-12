package com.progect.BankingApp.mapper;


import com.progect.BankingApp.dto.AccountDto;
import com.progect.BankingApp.entity.Account;

public class AccountMapper {

    // تحويل من AccountDto إلى Account Entity
    public static Account mapToAccount(AccountDto accountDto) {
        return Account.builder()
                .id(accountDto.getId())
                .cardNumber(accountDto.getCardNumber())
                .accountName(accountDto.getAccountName())
                .balance(accountDto.getBalance())
                .accountType(accountDto.getAccountType())
                .userId(accountDto.getUserId())
                .build();
    }

    // تحويل من Account Entity إلى AccountDto
    public static AccountDto mapToAccountDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getCardNumber(),
                account.getAccountName(),
                account.getBalance(),
                account.getAccountType(),
                account.getUserId()
        );
    }

    // تحويل من AccountDto إلى Account Entity مع userId محدد (للإنشاء)
    public static Account mapToAccountWithUserId(AccountDto accountDto, int userId) {
        return Account.builder()
                .accountName(accountDto.getAccountName())
                .accountType(accountDto.getAccountType())
                .balance(0.0) // دايماً صفر عند الإنشاء
                .userId(userId)
                // cardNumber هيتولد في الـ Service
                .build();
    }
}
