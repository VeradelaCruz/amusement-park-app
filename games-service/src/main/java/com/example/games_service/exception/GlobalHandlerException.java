package com.example.games_service.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Configuration
public class GlobalHandlerException {
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Map<String, String>> EmployeeNotFoundHandler(GameNotFoundException e) {
        Map<String, String> error = Map.of("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }
}
