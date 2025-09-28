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
    @Id
    private String gameId;
    @NotEmpty(message = "Name can not be empty")
    private String gameName;
    private int duration;
    private List<LocalTime> startTime;
    private List<LocalTime> endTime;

    //Getters and setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getNameGame() {
        return gameName;
    }

    public void setNameGame(String nameGame) {
        this.gameName = nameGame;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<LocalTime> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<LocalTime> startTime) {
        this.startTime = startTime;
    }

    public List<LocalTime> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<LocalTime> endTime) {
        this.endTime = endTime;
    }

}