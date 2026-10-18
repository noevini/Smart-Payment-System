package com.smartpaymentsystem.api.exceptionhandler;

public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
