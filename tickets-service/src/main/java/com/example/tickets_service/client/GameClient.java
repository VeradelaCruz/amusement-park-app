package com.example.tickets_service.client;

import com.example.tickets_service.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "games-service")
public interface GameClient {
    @GetMapping("/game/{gameId}")
    GameDTO getById(@PathVariable String gameId);
}
