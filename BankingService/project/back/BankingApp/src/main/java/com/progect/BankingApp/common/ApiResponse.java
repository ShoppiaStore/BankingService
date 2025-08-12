package com.progect.BankingApp.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
}
