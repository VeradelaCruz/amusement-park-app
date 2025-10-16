package com.example.buyer_service.controller;


import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerRankingDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.exception.GlobalHandlerException;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.service.BuyerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Carga solo el contexto web necesario para el controlador indicado (sin levantar toda la app).
@WebMvcTest(BuyerController.class)
@Import(GlobalHandlerException.class)
public class BuyerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permite hacer requests simuladas

    @MockBean
    private BuyerService buyerService; // Se simula el servicio que el controlador usa

    private ObjectMapper objectMapper = new ObjectMapper(); // para convertir objetos a JSON

    public Buyer buyer1;
    public Buyer buyer2;

    @BeforeEach
    void setUp() {
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

    }

    @Test
        //En tests unitarios, rara vez queremos capturar la excepción manualmente;
        //simplemente declaramos
        // throws Exception y dejamos que JUnit se encargue de marcarlo como error si ocurre.
    void addBuyer_ShouldReturnCreatedBuyer() throws Exception {
        when(buyerService.createBuyer(any(Buyer.class))).thenReturn(buyer1);

        mockMvc.perform(post("/buyer/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(buyerService).createBuyer(any(Buyer.class));
    }

    @Test
    void getById_WhenIdExists_ShouldReturnABuyer() throws Exception {
        when(buyerService.findById(buyer1.getBuyerId())).thenReturn(buyer1);

        mockMvc.perform(get("/buyer/byId/{buyerId}", "b1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"));

        verify(buyerService).findById("b1");
    }

    @Test
    void getById_WhenBuyerNotFound_ShouldReturnNotFoundMessage() throws Exception {
        when(buyerService.findById("x999"))
                .thenThrow(new BuyerNotFoundException("x999"));

        mockMvc.perform(get("/buyer/byId/{id}", "x999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Buyer with id: x999 not found."));
    }

    @Test
    void getAll_ShouldReturnAListOfBuyers() throws Exception {
        when(buyerService.findAll()).thenReturn(List.of(buyer1, buyer2));


        mockMvc.perform(get("/buyer/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteBuyer_ShouldReturnVoid() throws Exception {
        doNothing().when(buyerService).removeBuyer(buyer1.getBuyerId());

        mockMvc.perform(delete("/buyer/delete/{buyerId}", buyer1.getBuyerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Buyer removed successfully"));
    }

    @Test
    void updateBuyer_ShouldReturnADTO() throws Exception {
        // Arrange
        BuyerDTO expectedDTO = new BuyerDTO(
                "Alice",
                "Smith",
                "alice12@example.com", // el email actualizado
                "+34123456789",
                "DOC12345"
        );

        when(buyerService.changeBuyer(buyer1.getBuyerId(), expectedDTO)).thenReturn(expectedDTO);

        // Act + Assert
        mockMvc.perform(put("/buyer/update/{buyerId}", buyer1.getBuyerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedDTO))) // ← agregamos el cuerpo
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice12@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void getBuyerTotalSpent_ShouldReturnBuyerWithTotalAmount() throws Exception {
        // Arrange: creamos el objeto que el servicio debería devolver
        BuyerWithAmount responseDTO = BuyerWithAmount.builder()
                .buyerId("b1")
                .firstName("Alice")
                .lastName("Smith")
                .totalAmountSpent(200.0)
                .build();

        // Simulamos el comportamiento del servicio
        when(buyerService.findBuyerWithTotalAmount("b1")).thenReturn(responseDTO);

        // Act + Assert
        mockMvc.perform(get("/buyer/getBuyerTotalSpent/{buyerId}", "b1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value("b1"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.totalAmountSpent").value(200.0));
    }

    @Test
    void getBuyersRanking_ShouldReturnAListOfBuyers() throws Exception{
        BuyerRankingDTO buyerRankingDTO1= BuyerRankingDTO.builder()
                .buyerId("b1")
                .firstName("Alice")
                .lastName("Smith")
                .ticketsCount(2)
                .build();
        BuyerRankingDTO buyerRankingDTO2= BuyerRankingDTO.builder()
                .buyerId("b2")
                .firstName("Bob")
                .lastName("Jhonson")
                .ticketsCount(1)
                .build();

        when(buyerService.getBuyerRanking()).thenReturn(List.of(buyerRankingDTO1,buyerRankingDTO2));

        mockMvc.perform(get("/buyer/getBuyersRanking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].ticketsCount").value(2))
                .andExpect(jsonPath("$[1].ticketsCount").value(1));

    }




}
