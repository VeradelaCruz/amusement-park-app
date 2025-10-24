package com.example.tickets_service.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;


    public TicketProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketPurchased(String ticketId) {
        kafkaTemplate.send("tickets", ticketId);
        System.out.println("New ticket purchased: " + ticketId);
    }
}
