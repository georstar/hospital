package com.h.operationservice.exception;

public class RequestAllreadyCompleted extends RuntimeException {
    public RequestAllreadyCompleted(String message) {
        super(message);
    }
}
