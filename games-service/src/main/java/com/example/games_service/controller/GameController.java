package com.example.games_service.controller;

import com.example.games_service.dtos.GameDTO;
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

    @PostMapping("/add")
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game){
        Game saved= gameService.createGame(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{gameId}")
    public Game getById(@PathVariable String gameId){
        return gameService.findById(gameId);
    }

    @GetMapping("/all")
    public List<Game> getAll(){
        return gameService.findAll();
    }

    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable String gameId){
        gameService.removeById(gameId);
        return ResponseEntity.ok("Game removed successfully");
    }

    @PutMapping("/update/{gameId}")
    public ResponseEntity<GameDTO> updateGame(
            @PathVariable String gameId,
            @RequestBody GameDTO gameDTO){
        GameDTO updated= gameService.changeGame(gameId, gameDTO);
        return ResponseEntity.ok(updated);
    }


}
