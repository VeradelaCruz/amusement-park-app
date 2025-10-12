package com.example.games_service.client;

import com.example.games_service.dtos.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "tickets-service")
public interface TicketClient {

    @GetMapping("/tickets/id")
    TicketDTO getById(String ticketId);

    @GetMapping("/tickets/all")
    List<TicketDTO> getAll();

}
