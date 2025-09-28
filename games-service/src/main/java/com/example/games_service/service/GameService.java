package com.example.games_service.service;

import com.example.games_service.models.Game;
import com.example.games_service.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public Game createGame(Game game){
        return gameRepository.save(game);
    }
}
