package com.example.games_service.service.kafka;

import com.example.games_service.service.GameService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class GameConsumer {

    private final GameService gameService; // inyectamos tu servicio principal

    public GameConsumer(GameService gameService) {
        this.gameService = gameService;
    }

    @KafkaListener(topics = "tickets", groupId = "games-service-group")
    public void onTicketPurchased(String message) {
        System.out.println("🎮 Received even in games-service: " + message);

        try {
            // Parsear el JSON recibido
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(message);

            String gameId = node.get("gameId").asText();
            Double price = node.get("price").asDouble();

            // Recalcular ingresos totales
            gameService.findGameWithTotalSell(gameId);

            System.out.println("💰 Earns for game: " + gameId);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
