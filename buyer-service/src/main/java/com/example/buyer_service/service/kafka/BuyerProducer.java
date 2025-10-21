package com.example.buyer_service.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BuyerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BuyerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBuyerEvent(String buyerId) {
        kafkaTemplate.send("buyers", buyerId);
        System.out.println("Evento enviado: " + buyerId);
    }
}
