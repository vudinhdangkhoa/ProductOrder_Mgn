package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message, long resourceId) {
        super(message);
    }
}
