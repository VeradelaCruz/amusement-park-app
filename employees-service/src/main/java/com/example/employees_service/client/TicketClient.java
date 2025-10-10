package com.example.employees_service.client;

import com.example.employees_service.dtos.TicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "tickets-service", url = "http://localhost:8083")
public interface TicketClient {
    @GetMapping("/ticket/all")
    List<TicketDTO> getAll();
}
