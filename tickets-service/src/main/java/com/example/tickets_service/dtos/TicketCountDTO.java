package com.example.tickets_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCountDTO {
    private Long amount;
    private LocalDate date;
    private GameDTO game;
}

