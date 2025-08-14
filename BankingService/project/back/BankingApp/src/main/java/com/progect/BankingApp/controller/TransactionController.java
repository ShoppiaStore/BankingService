package com.progect.BankingApp.controller;

import com.progect.BankingApp.common.ApiResponse;
import com.progect.BankingApp.dto.AccountDto;
import com.progect.BankingApp.dto.TransactionDto;
import com.progect.BankingApp.dto.TransactionRequest;
import com.progect.BankingApp.entity.Account;
import com.progect.BankingApp.service.AccountService;
import com.progect.BankingApp.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8080/validateToken";
    private static final String USER_SERVICE_URL2 = "http://localhost:8080/extractUserId";

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    private AccountService accountService;

    // Deposit money
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(@RequestBody TransactionRequest transactionRequest
         ) {
        try {

            String cardNumber = transactionRequest.getCardNumber();
            double amount = transactionRequest.getAmount();
            log.info("you want to deposit Amount {}, for customer with card Number {}",
                    amount, cardNumber);

            TransactionDto transaction = transactionService.deposit( cardNumber,  amount);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Transaction deposited successfully", transaction));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse(false, e.getStatusText(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while processing the deposit", null));
        }
    }

    // Withdraw money
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse> withdraw(
            @RequestBody TransactionRequest transactionRequest
            ) {
        try {


            String cardNumber = transactionRequest.getCardNumber();
            double amount = transactionRequest.getAmount();

            TransactionDto transaction = transactionService.withdraw(cardNumber, amount);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Withdrawal completed successfully", transaction));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while processing the withdrawal", null));
        }
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<ApiResponse> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestHeader("Authorization") String token) {
        try {
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid or expired token", null));
            }

            AccountDto accountDto = accountService.getAccountById(accountId);
            ResponseEntity<Integer> response = getUserId(token);
            int userIdFromToken = response.getBody();

            if (userIdFromToken != accountDto.getUserId()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "You are not authorized to access this account's history", null));
            }

            List<TransactionDto> transactions = transactionService.getTransactionHistory(accountId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "Transaction history retrieved successfully", transactions));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse(false, e.getStatusText(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving transaction history", null));
        }
    }

    private boolean isTokenValid(String token) {

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {

            ResponseEntity<String> response = restTemplate.exchange(
                    USER_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return "valid token".equalsIgnoreCase(response.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode());
            System.out.println("Error Body: " + e.getResponseBodyAsString());
            return false;
        }
    }

    private ResponseEntity<Integer> getUserId(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    USER_SERVICE_URL2,
                    HttpMethod.POST,
                    entity,
                    Integer.class
            );
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(0);
        }
    }
}