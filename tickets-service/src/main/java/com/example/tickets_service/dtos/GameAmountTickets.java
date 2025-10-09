package com.example.tickets_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameAmountTickets {
    private String gameId;
    private String gameName;
    private Long amountOfTickets;
    private Double totalPrice;


}
