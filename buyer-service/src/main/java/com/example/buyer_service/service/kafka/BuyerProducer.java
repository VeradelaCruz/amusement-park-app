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

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BuyerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBuyerEvent(String buyerId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Crear un objeto JSON con el buyerId
            Map<String, String> payload = Map.of("buyerId", buyerId);
            String json = objectMapper.writeValueAsString(payload);

            // Enviar el JSON al topic
            kafkaTemplate.send("buyer-topic", json);

            System.out.println("Evento enviado: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Para actualizar un buyer
    public void sendBuyerUpdateEvent(String buyerId, String firstName) {
        kafkaTemplate.send("buyer-topic", "Buyer updated: " + buyerId + " - " + firstName);
        System.out.println("Evento enviado: Buyer updated " + buyerId);
    }
}
