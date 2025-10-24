package com.example.tickets_service.service.kafka;

import com.example.tickets_service.models.Ticket;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;


    public TicketProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketPurchased(Ticket ticket) {
        // Crear mensaje JSON con toda la info relevante
        String message = String.format(
                "{\"ticketId\":\"%s\",\"buyerId\":\"%s\",\"gameId\":\"%s\",\"time\":\"%s\",\"date\":\"%s\",\"price\":%.2f}",
                ticket.getTicketId(),
                ticket.getBuyerId(),
                ticket.getGameId(),
                ticket.getTime(),
                ticket.getDate(),
                ticket.getPrice()
        );

        // Enviar mensaje al topic "tickets"
        kafkaTemplate.send("tickets", message);
        System.out.println("Evento enviado a Kafka: " + message);
    }
}
