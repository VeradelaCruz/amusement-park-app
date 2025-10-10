package com.example.tickets_service.repository;

import com.example.tickets_service.models.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
     List<Ticket> findByBuyerId(String buyerId);
}
