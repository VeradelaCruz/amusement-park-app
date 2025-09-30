package com.example.games_service.service;

import com.example.games_service.dtos.GameDTO;
import com.example.games_service.exception.GameNotFoundException;
import com.example.games_service.mapper.GameMapper;
import com.example.games_service.models.Game;
import com.example.games_service.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;


    //CRUD OPERATIONS
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

}
