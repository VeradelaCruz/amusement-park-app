package com.example.games_service.service;

import com.example.games_service.client.TicketClient;
import com.example.games_service.dtos.GameDTO;
import com.example.games_service.dtos.GameWithAmount;
import com.example.games_service.dtos.TicketDTO;
import com.example.games_service.dtos.TicketWithPrice;
import com.example.games_service.exception.GameNotFoundException;
import com.example.games_service.mapper.GameMapper;
import com.example.games_service.models.Game;
import com.example.games_service.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private TicketClient ticketClient;



    //--------CRUD OPERATIONS------------
    @CacheEvict(value = {"games", "gamesWithTotalSale","gamesWithTIme"}, allEntries = true)
    public Game createGame(Game game){
        return gameRepository.save(game);
    }

    @Cacheable(value = "games", key = "#gameId")
    public Game findById(String gameId){
        return gameRepository.findById(gameId)
                .orElseThrow(()-> new GameNotFoundException(gameId));
    }

    @Cacheable(value = "games")
    public List<Game> findAll(){
        return gameRepository.findAll();
    }

    @CacheEvict(value = {"games", "gamesWithTotalSale","gamesWithTIme"}, allEntries = true)
    public void removeById(String gameId){
        Game game= findById(gameId);
        gameRepository.deleteById(game.getGameId());
    }

    //changeGame() actualiza el juego individual (@CachePut) y limpia las cachés derivadas.
    @CachePut(value="games", key="#gameId")
    @CacheEvict(value={"gamesWithTotalSell","gamesByTime"}, allEntries=true)
    public GameDTO changeGame(String gameId, GameDTO gameDTO){
        //Busca el que se va a actualizar:
        Game existing= findById(gameId);

        //Actualiza:
        gameMapper.updateFromDTO(gameDTO,existing);

        //Guarda en la BD
        Game saved = gameRepository.save(existing);

        //Devuelve el dto actualizado:
        return  gameMapper.toDTO(saved);
    }

    //------OTHER OPERATIONS-----
    //Obtener juego con ingresos totales

    //si se venden nuevos tickets o cambian precios:
    @CacheEvict(value="gamesWithTotalSell", allEntries=true)
    public GameWithAmount findGameWithTotalSell(String gameId){
        //Buscamos el juego con el id dado:
        Game game= findById(gameId);

        //Traemos los tickets del microservicio:
        Double totalPrice= ticketClient.getAll()
        //Buscamos los tickets que coinciden con el gameId
                .stream()
                .filter(ticketDTO -> ticketDTO.getGameId().equals(gameId))
                .mapToDouble(TicketDTO::getPrice)
                .sum();
        //Retornamos el dto construido:
        return GameWithAmount.builder()
                .gameId(gameId)
                .gameName(game.getGameName())
                .totalSell(totalPrice)
                .build();
    }

    //Juegos activos en un horario determinado

    //Si cambian horarios de juegos o se agregan/eliminan juegos, la caché puede quedar obsoleta.
    //
    //Ideal: limpiar la caché con @CacheEvict(value="gamesByTime",
    // allEntries=true) en métodos que alteran horarios o crean/eliminan juegos.
    @CacheEvict(value="gamesByTime", allEntries=true)
    public List<GameDTO> findGamesByTime(LocalTime time) {
        return findAll().stream()
                .filter(game ->
                        (game.getStartTime().equals(time) || game.getStartTime().isBefore(time)) &&
                                (game.getEndTime().equals(time) || game.getEndTime().isAfter(time))
                )
                .map(gameMapper::toDTO)
                .collect(Collectors.toList());
    }


}
