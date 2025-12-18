package com.h.operationservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("IDEMPOTENCY_KEY")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "status", 409,
                            "error", "Conflict",
                            "message", "Operation with this idempotency key already exists"
                    ));
        }

        // fallback for other integrity violations
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMostSpecificCause().getMessage()
                ));
    }

    @ExceptionHandler(RequestAllreadyCompleted.class)
    public ResponseEntity<Map<String, String>> handleRequestAlreadyCompleted(RequestAllreadyCompleted ex) {
        log.error("Request already completed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Request has already been completed.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(OperationTypeForPatientNotFoundToUndone.class)
    public ResponseEntity<Map<String, String>> handleOperationTypeForPatientNotFoundToUndone(OperationTypeForPatientNotFoundToUndone ex) {
        log.error("OperationTypeForPatientNotFoundToUndone: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "OperationType for patient id not found to undone");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }
}
