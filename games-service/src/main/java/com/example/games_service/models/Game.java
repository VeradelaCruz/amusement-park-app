package com.example.games_service.models;

import com.example.games_service.validation.ValidTimeRange;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;
@Data
@Document(collection = "games")
@ValidTimeRange
public class Game {

    @Id
    private String gameId;

    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 50, message = "Game name cannot exceed 50 characters")
    private String gameName;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 30, message = "Duration cannot exceed 30 minutes")
    private int duration;

    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    private Double priceGame;


    public String getNameGame() {
        return gameName;
    }

    public void setNameGame(String nameGame) {
        this.gameName = nameGame;
    }

}