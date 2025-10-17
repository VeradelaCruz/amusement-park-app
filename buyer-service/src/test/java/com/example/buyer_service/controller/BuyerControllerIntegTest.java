package com.example.buyer_service.controller;

import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Levanta toda la lógica completa y su conexión real
@SpringBootTest
//Configura MockMvc automáticamente cuando se usa @SpringBootTest.
//Permite usar MockMvc para hacer llamadas HTTP simuladas.
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BuyerControllerIntegTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuyerRepository buyerRepository;

    private Buyer buyer1;
    private Buyer buyer2;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        buyerRepository.deleteAll(); // Limpiamos la tabla

        // Limpiar DB antes de correr tests
        buyerRepository.deleteAll();

        // Crear buyers con ID fija
        buyer1 = new Buyer();
        buyer1.setBuyerId("b1");
        buyer1.setFirstName("Alice");
        buyer1.setLastName("Smith");
        buyer1.setEmail("alice@example.com");
        buyer1.setPhone("+34123456789");
        buyer1.setDocumentId("DOC12345");

        buyer2 = new Buyer();
        buyer2.setBuyerId("b2");
        buyer2.setFirstName("Bob");
        buyer2.setLastName("Johnson");
        buyer2.setEmail("bob@example.com");
        buyer2.setPhone("+34198765432");
        buyer2.setDocumentId("DOC67890");

        buyerRepository.saveAll(List.of(buyer1, buyer2));
    }

//Integration test: más realista,
// ideal para validar que todos los componentes cooperan correctamente.
//La sintaxis es muy parecida, pero cambian las anotaciones y el alcance del contexto.

    @Test
    void addBuyer_ShouldReturnCreatedBuyer() throws Exception{
        mockMvc.perform(post("/buyer/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
}
