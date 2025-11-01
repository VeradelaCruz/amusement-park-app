package com.example.buyer_service.service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
//Es el servicio que vos usás en tu código.
//Depende del KafkaTemplate que ya configuraste.
//Este sí envía los mensajes reales a un topic
public class BuyerProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BuyerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBuyerEvent(String buyerId) {
        kafkaTemplate.send("buyer-topic", buyerId);
        System.out.println("Evento enviado: " + buyerId);
    }

    // Para actualizar un buyer
    public void sendBuyerUpdateEvent(String buyerId, String firstName) {
        kafkaTemplate.send("buyer-topic", "Buyer updated: " + buyerId + " - " + firstName);
        System.out.println("Evento enviado: Buyer updated " + buyerId);
    }
}
