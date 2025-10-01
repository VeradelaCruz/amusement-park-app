package com.example.tickets_service.exception;

public class GameNotAvailableException extends RuntimeException {
    public GameNotAvailableException(String gameName){
        super("Game: "+ gameName + " is not available at this time");
    }
}
