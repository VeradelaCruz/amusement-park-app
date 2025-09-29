package com.example.tickets_service.exception;

public class GameNotAvailableException extends RuntimeException {
    public GameNotAvailableException(String gameName){
        super("Ticket with id: "+ gameName + " not found.");
    }
}
