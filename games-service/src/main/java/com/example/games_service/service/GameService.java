package com.example.games_service.service;

import com.example.games_service.exception.GameNotFoundException;
import com.example.games_service.models.Game;
import com.example.games_service.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

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

}
