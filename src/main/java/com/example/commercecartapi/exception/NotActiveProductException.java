package com.example.commercecartapi.exception;

public class NotActiveProductException extends RuntimeException {
    public NotActiveProductException(String message) {
        super(message);
    }
}
