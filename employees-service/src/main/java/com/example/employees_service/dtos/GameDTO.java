package com.example.employees_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO {
    private String gameId;
    private String gameName;
    private int duration;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double priceGame;

}
