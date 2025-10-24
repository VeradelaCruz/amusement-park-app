package com.example.tickets_service.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TicketConsumer {
    
    @KafkaListener(topics = "tickets", groupId = "ticket-service-group")
    public void listenBuyerEvent(String message) {
        System.out.println("Received event in tickets-service: " + message);
    }
}
