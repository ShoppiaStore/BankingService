package com.progect.BankingApp.Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String massage) {
        super(massage);
    }
}
