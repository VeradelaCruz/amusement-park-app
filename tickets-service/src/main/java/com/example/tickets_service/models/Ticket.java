package com.example.tickets_service.models;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String ticketId;
    @NotBlank(message = "Game ID cannot be empty")
    private String gameId;

    @NotBlank(message = "Buyer ID cannot be empty")
    private String buyerId;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate date;

    @NotNull(message = "Time cannot be null")
    private LocalTime time;

    @Positive(message = "Price must be greater than 0")
    private double price;

}
