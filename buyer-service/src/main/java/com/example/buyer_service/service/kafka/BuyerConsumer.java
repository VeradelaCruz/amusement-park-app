package com.example.buyer_service.service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
//Es básicamente el receptor de los mensajes que envía BuyerProducer.
//Mientras que BuyerProducer produce mensajes y los manda a un topic en Kafka,
//BuyerConsumer escucha ese topic y hace algo con los mensajes que llegan.
public class BuyerConsumer {

    @KafkaListener(topics = "buyer-topic", groupId = "buyer-service-group")
    public void listenBuyerEvent(String message) {
        System.out.println("Evento recibido en buyer-service: " + message);
    }
}
