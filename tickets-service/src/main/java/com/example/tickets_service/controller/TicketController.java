package com.example.tickets_service.controller;

import com.example.tickets_service.models.Ticket;
import com.example.tickets_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/addTicket")
    public ResponseEntity<?> addTicket(@Valid @RequestBody Ticket ticket){
        ticketService.createTicket(ticket);
        return ResponseEntity.ok("Ticket added.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String ticketId){
        return ResponseEntity.ok(ticketService.findById(ticketId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(ticketService.findAll());
    }
}
