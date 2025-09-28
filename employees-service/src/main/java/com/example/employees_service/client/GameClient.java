package com.example.employees_service.client;

import com.example.employees_service.dtos.GameDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "games-service")
public interface GameClient {
    @GetMapping("/games/{id}")
    GameDTO getGameById(@PathVariable String gameId);
}
