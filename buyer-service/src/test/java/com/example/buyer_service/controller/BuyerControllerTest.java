package com.example.buyer_service.controller;


import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.service.BuyerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Carga solo el contexto web necesario para el controlador indicado (sin levantar toda la app).
@WebMvcTest(BuyerController.class)
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


}
