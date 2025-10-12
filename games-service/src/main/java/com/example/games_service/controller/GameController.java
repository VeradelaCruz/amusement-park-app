package com.example.games_service.controller;

import com.example.games_service.dtos.GameDTO;
import com.example.games_service.dtos.GameWithAmount;
import com.example.games_service.models.Game;
import com.example.games_service.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
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

    @GetMapping("/game-totalPrice/{gameId}")
    public ResponseEntity<GameWithAmount> getGameWithTotalSell(@PathVariable String gameId) {
        // Usamos try-catch por si el juego no existe o hay error con el microservicio
        try {
            GameWithAmount result = gameService.findGameWithTotalSell(gameId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // Si no se encuentra el juego, devolvemos un 404 con el mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameDTO>> getActiveGames(
            @RequestParam("time") String timeParam) {

        try {
            // Convertimos el parámetro String a LocalTime
            LocalTime time = LocalTime.parse(timeParam);

            // Obtenemos la lista de juegos activos
            List<GameDTO> games = gameService.findGamesByTime(time);

            // Si la lista está vacía, devolvemos 204 No Content
            if (games.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(games);

        } catch (DateTimeParseException e) {
            // Si la hora no tiene el formato correcto (por ejemplo 14:30)
            return ResponseEntity.badRequest()
                    .body(Collections.emptyList());
        }
    }

}
