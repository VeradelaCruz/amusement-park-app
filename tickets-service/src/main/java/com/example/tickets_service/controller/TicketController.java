package com.example.tickets_service.controller;

import com.example.tickets_service.dtos.TicketDTO;
import com.example.tickets_service.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String ticketId){
        return ResponseEntity.ok(ticketService.findById(ticketId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(ticketService.findAll());
    }

    @PostMapping("/sellTicket/{buyerId}/{gameId}")
    public ResponseEntity<TicketDTO> giveTicket(
            @PathVariable String buyerId,
            @PathVariable String gameId) {
        TicketDTO ticketDTO = ticketService.sellTicket(gameId, buyerId);
        return ResponseEntity.ok(ticketDTO);
    }
}
