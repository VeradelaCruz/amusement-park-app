package com.example.tickets_service.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Document(collection = "tickets")
public class Ticket {
    private String id;
    private String gameId;
    private String buyerId;
    private LocalDate date;
    private LocalTime time;
    private double price;

}
