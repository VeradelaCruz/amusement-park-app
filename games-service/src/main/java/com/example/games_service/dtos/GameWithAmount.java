package com.example.games_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameWithAmount {
    private String gameId;
    private String gameName;
    private Double priceGame;
    private Double totalSell;
}
