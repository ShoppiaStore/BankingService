package com.progect.BankingApp.dto;

//import jakarta.validation.constraints.DecimalMin;
//import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

   // @Size(min = 16, max = 16, message = "card number must be 16 characters long")
    private String cardNumber;

 //   @DecimalMin(value = "0.01", message = "Amount must be greater than or equal to 0.01")
    private double amount;
}
