package com.example.buyer_service.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Configuration
public class GlobalHandlerException {
public ResponseEntity<Map<String, String>> BuyerNotFoundException(BuyerNotFoundException e){
   Map<String, String> error= Map.of("message", e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

}
}
