package com.example.demo.Exceptions;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String msg){
        super(msg);
    }
}
