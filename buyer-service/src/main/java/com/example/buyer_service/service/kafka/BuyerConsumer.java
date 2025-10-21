package com.example.buyer_service.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BuyerConsumer {

    @KafkaListener(topics = "buyers", groupId = "buyer-service-group")
    public void listenBuyerEvent(String message) {
        System.out.println("Evento recibido en buyer-service: " + message);
    }
}
