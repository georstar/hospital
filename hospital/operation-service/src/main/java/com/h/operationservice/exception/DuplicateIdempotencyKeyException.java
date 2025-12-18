package com.h.operationservice.exception;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException(String key) {
        super("Operation with idempotency key '" + key + "' already exists.");
    }
}
