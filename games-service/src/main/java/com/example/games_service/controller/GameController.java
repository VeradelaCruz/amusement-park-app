package com.example.games_service.controller;

import com.example.games_service.models.Game;
import com.example.games_service.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game){
        Game saved= gameService.createGame(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public Game getById(@PathVariable String gameId){
        return gameService.findById(gameId);
    }

    @GetMapping("/all")
    public List<Game> getAll(){
        return gameService.findAll();
    }
}
