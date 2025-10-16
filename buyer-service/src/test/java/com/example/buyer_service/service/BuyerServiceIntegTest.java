package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test") // Opcional: para usar application-test.properties
@AutoConfigureDataMongo
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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


    @Test
    void createBuyer_WhenValidInput_ShouldReturnBuyer() {
        //Ya no se usa when porque se levanta todo el contexto,
        // no hay mocks porque no simula ningun comportamiento, todo es real
        Buyer buyer= buyerService.createBuyer(buyer1);

        assertThat(buyer).isNotNull();
        assertThat(buyer.getBuyerId()).isEqualTo("b1");
    }

    @Test
    void findById_WhenExists_ShouldReturnBuyer(){
        Buyer buyer= buyerService.findById(buyer1.getBuyerId());

        assertThat(buyer).isNotNull();
        assertThat(buyer.getBuyerId()).isEqualTo("b1");
        assertThat(buyer.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void findById_WhenDoesNotExists_ShouldReturnBuyer(){
        // Arrange: no insertamos nada en la DB para el id "99L"

        // Act & Assert
        assertThatThrownBy(() -> buyerService.findById("99L"))
                .isInstanceOf(BuyerNotFoundException.class)
                .hasMessageContaining("Buyer with id: 99L not found");
    }



}
