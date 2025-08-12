package com.progect.BankingApp.repositry;

import com.progect.BankingApp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {


    boolean existsByCardNumber(String cardNumber);

    List<Account> findByUserId(int userId);
    Optional<Account> findByCardNumber(String cardNumber);
}
