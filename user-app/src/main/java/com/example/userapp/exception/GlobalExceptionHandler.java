package com.example.userapp.exception;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignStatusException(FeignException e) {
        return ResponseEntity.status(e.status()).body(e.getMessage());
    }
}
