package com.progect.BankingApp.dto;


import com.progect.BankingApp.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {


    private Long id;

    private String cardNumber;

    @NotBlank(message = "Account name is required")
    private String accountName;

    private double balance;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private int userId;

}
