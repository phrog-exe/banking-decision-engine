package com.example.demo.exception;

public class UnknownApplicantException extends RuntimeException {
    public UnknownApplicantException(String message) {
        super(message);
    }
}
