package com.example.employees_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private String ticketId;
    private String gameId;
    private String buyerId;
    private LocalDate date;
    private LocalTime time;
    private double price;
}