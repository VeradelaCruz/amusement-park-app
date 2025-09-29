package com.example.buyer_service.exception;

public class BuyerNotFoundException extends RuntimeException {
    public BuyerNotFoundException(String buyerId) {
        super("Buyer with id: "+ buyerId + " not found.");
    }
}
