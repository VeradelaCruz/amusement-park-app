package com.example.buyer_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

//Se usa para crear un manejador global de excepciones (un exception handler global)
// que escucha todos los errores lanzados por tus controladores REST.
@RestControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(BuyerNotFoundException.class)
        public ResponseEntity<Map<String, String>> BuyerNotFoundException(BuyerNotFoundException e){
        Map<String, String> error= Map.of("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }
}
