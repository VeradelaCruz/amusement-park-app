package com.example.tickets_service.controller;

import com.example.tickets_service.dtos.*;
import com.example.tickets_service.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @DeleteMapping("/delete/{ticketId}")
    public ResponseEntity<?> deleteTicket(@PathVariable String ticketId){
        ticketService.removeTicket(ticketId);
        return ResponseEntity.ok("Ticket removed successfully");
    }

    @PutMapping("/update/{ticketId}")
    public ResponseEntity<?> updateTicket(
            @PathVariable String ticketId,
            @RequestBody TicketDTO ticketDTO){
        TicketDTO updated= ticketService.changeTicket(ticketId, ticketDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/countTickets/{date}")
    public ResponseEntity<?> countTickets(
            //convierte el string de la url en LocalDate
             @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<TicketCountDTO> result= ticketService.ticketAmount(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/countGameTickets/{gameId}/{date}")
    public ResponseEntity<?> countGameTickets(
            //convierte el string de la url en LocalDate
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String gameId,
            LocalDate date
            ){
        TicketCountDTO result= ticketService.gameTicketAmount(gameId, date );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAllTicketsAmount/{date}")
    public SalesTotalDTO countAllTicketsAmount(
            @PathVariable @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date){
        return ticketService.countAllTickets(date);
    }

    @GetMapping("/getTicketsByMonthYear/{month}/{year}")
    public SalesTotalMonthYearDTO getTicketsByMonthYear(
            @PathVariable int month,
            @PathVariable int year){
        return ticketService.countByMonthAndYear(month, year);
    }

    @GetMapping("/max-buyer")
    public BuyerWithTicket getBuyerWithTicket(){
        return ticketService.getTopBuyerWithTicket();
    }


    @GetMapping("/max-tickets")
    public ResponseEntity<GameAmountTickets> getMaxAmountTicket() {
        return ResponseEntity.ok(ticketService.getTopGameWithTicket());
    }

    @GetMapping("/avg-price")
    public List<GameWithAverageDTO> getAverageTicketPricePerGame() {
        return ticketService.getAverageGame();
    }

}
