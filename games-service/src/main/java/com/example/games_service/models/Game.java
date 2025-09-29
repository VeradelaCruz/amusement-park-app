package com.example.games_service.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;

@Data
@Document(collection = "games")
public class Game {
    //Getters and setters
    @Id
    private String gameId;
    @NotEmpty(message = "Name can not be empty")
    private String gameName;
    private int duration;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double priceGame;


    public String getNameGame() {
        return gameName;
    }

    public void setNameGame(String nameGame) {
        this.gameName = nameGame;
    }

}