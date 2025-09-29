package com.example.tickets_service.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String ticketId) {
        super("Ticket with id: "+ ticketId + " not found.");
    }
}
