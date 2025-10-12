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
import org.springframework.stereotype.Service;

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
    public Game createGame(Game game){
        return gameRepository.save(game);
    }

    public Game findById(String gameId){
        return gameRepository.findById(gameId)
                .orElseThrow(()-> new GameNotFoundException(gameId));
    }

    public List<Game> findAll(){
        return gameRepository.findAll();
    }

    public void removeById(String gameId){
        Game game= findById(gameId);
        gameRepository.deleteById(game.getGameId());
    }

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
}
