package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test") // Opcional: para usar application-test.properties
@Transactional // Hace rollback después de cada test
public class BuyerServiceIntegTest {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private BuyerRepository buyerRepository;


    //Cuando hacemos pruebas de integración del service,
    // queremos probar la lógica de tu service con la base de datos real (H2),
    // pero no queremos depender del otro microservicio real para los tests, porque:
    //Puede que no esté corriendo cuando corren los tests.
    //Queremos que los tests sean rápidos y confiables.
    //Esto le dice a Spring:
    //“En el contexto de pruebas, en vez de usar el Feign real,
    // poneme un mock que yo pueda controlar con when(...).thenReturn(...).”
    @MockBean
    private TicketClient ticketClient;

    private Buyer buyer1;
    private Buyer buyer2;

    @BeforeEach
    void setUp() {
        buyerRepository.deleteAll(); // Limpiamos la tabla

        buyer1 = new Buyer("b1", "Alice", "Smith", "alice@example.com", "+34123456789", "DOC12345");
        buyer2 = new Buyer("b2", "Bob", "Johnson", "bob@example.com", "+34198765432", "DOC67890");

        buyerRepository.saveAll(List.of(buyer1, buyer2));
    }



}
