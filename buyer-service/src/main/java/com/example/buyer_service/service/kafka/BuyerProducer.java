package com.example.buyer_service.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
//Es el servicio que vos usás en tu código.
//Depende del KafkaTemplate que ya configuraste.
//Este sí envía los mensajes reales a un topic
public class BuyerProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BuyerProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBuyerEvent(String buyerId) {
        Map<String, String> payload = Map.of("buyerId", buyerId);
        kafkaTemplate.send("buyer-topic-json", payload);
        System.out.println("Evento enviado: " + payload);
    }



    // Para actualizar un buyer
    public void sendBuyerUpdateEvent(String buyerId, String firstName) {
        kafkaTemplate.send("buyer-topic-json", "Buyer updated: " + buyerId + " - " + firstName);
        System.out.println("Evento enviado: Buyer updated " + buyerId);
    }
}
