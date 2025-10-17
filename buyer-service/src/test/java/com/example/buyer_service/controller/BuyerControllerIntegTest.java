package com.example.buyer_service.controller;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.dtos.TicketDTO;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private TicketClient ticketClient;

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

    @Test
    void getById_WhenIdExists_ShouldReturnABuyer() throws Exception{
        mockMvc.perform(get("/buyer/byId/{buyerId}", "b1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"));
    }

    @Test
    void getById_WhenBuyerNotFound_ShouldReturnNotFoundMessage() throws Exception {
        mockMvc.perform(get("/buyer/byId/{id}", "x999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Buyer with id: x999 not found."));
    }

    @Test
    void getAll_ShouldReturnAListOfBuyers() throws Exception{
        mockMvc.perform(get("/buyer/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteBuyer_ShouldReturnVoid() throws Exception {
        mockMvc.perform(delete("/buyer/delete/{buyerId}", buyer1.getBuyerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Buyer removed successfully"));
    }

    @Test
    void updateBuyer_ShouldReturnADTO() throws Exception {
        BuyerDTO expectedDTO = new BuyerDTO(
                "Alice",
                "Smith",
                "alice12@example.com", // el email actualizado
                "+34123456789",
                "DOC12345"
        );

        mockMvc.perform(put("/buyer/update/{buyerId}", buyer1.getBuyerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDTO))) // ← agregamos el cuerpo
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice12@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void getBuyerTotalSpent_ShouldReturnBuyerWithTotalAmount() throws Exception{
        //Simular ticketClient:
        TicketDTO ticketDTO1 = new TicketDTO(
                "T1",
                "G1",
                "b1",
                LocalDate.of(2025, 10, 1),
                LocalTime.of(9, 00, 00),
                10.0
        );
        TicketDTO ticketDTO2 = new TicketDTO(
                "T2",
                "G2",
                "b1",
                LocalDate.of(2025, 10, 1),
                LocalTime.of(10, 00, 00),
                10.0
        );

        when(ticketClient.getByBuyerId(buyer1.getBuyerId()))
                .thenReturn(List.of(ticketDTO1,ticketDTO2));

        // Act + Assert
        mockMvc.perform(get("/buyer/getBuyerTotalSpent/{buyerId}", "b1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.totalAmountSpent").value(20.0));
    }
}

