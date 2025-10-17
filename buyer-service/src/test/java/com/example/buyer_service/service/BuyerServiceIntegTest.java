package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerRankingDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.dtos.TicketDTO;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
        //método de AssertJ que se usa cuando quieres probar que una acción lanza una excepción.
        //Lo que va dentro del paréntesis es un lambda:
        assertThatThrownBy(() -> buyerService.findById("99L"))
                //Este método verifica el tipo de la excepción que fue lanzada.
                //En este caso, estamos diciendo:
                // “Espero que la excepción que se lance sea exactamente de tipo BuyerNotFoundException”.
                .isInstanceOf(BuyerNotFoundException.class)
                //Este método verifica el mensaje de la excepción.
                //.hasMessageContaining(...) permite chequear parte del mensaje,
                // no necesita ser exacto.
                .hasMessageContaining("Buyer with id: 99L not found");
    }

    @Test
    void findAll_ShouldReturnAList(){

        //Act
        List<Buyer> list= buyerService.findAll();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getBuyerId()).isEqualTo("b1");
        assertThat(list.get(1).getBuyerId()).isEqualTo("b2");
    }

    @Test
    void removeBuyer_WhenIsPresent_ShouldReturnVoid(){
        buyerService.removeBuyer(buyer1.getBuyerId());

        // Assert: verificar que ya no está en la BD
        boolean exists = buyerRepository.findById(buyer1.getBuyerId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    void removeBuyer_WhenIsNotPresent_ShouldReturnVoid(){
        assertThatThrownBy(()->buyerService.removeBuyer("99L"))
                .isInstanceOf(BuyerNotFoundException.class)
                .hasMessageContaining("Buyer with id: 99L not found.");
    }

    @Test
    void changeBuyer_ShouldReturnDTO(){
        //Arrange
        BuyerDTO expectedDTO = new BuyerDTO(
                "Alice",
                "Smith",
                "alice12@example.com", // el email actualizado
                "+34123456789",
                "DOC12345"
        );

        //Act
        BuyerDTO updatedBuyer= buyerService.changeBuyer(buyer1.getBuyerId(), expectedDTO);

        //Assert
        assertThat(updatedBuyer).isNotNull();
        assertThat(updatedBuyer.getEmail()).isEqualTo("alice12@example.com");

    }

    @Test
    void findBuyerWithTotalAmount_ShouldReturnADTO(){
        //Simular MockBean el llamado al otro microservicio (tickets)
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

        //Act
        BuyerWithAmount buyer= buyerService.findBuyerWithTotalAmount(buyer1.getBuyerId());

        //Assert
        assertThat(buyer).isNotNull();
        assertThat(buyer.getBuyerId()).isEqualTo("b1");
        assertThat(buyer.getTotalAmountSpent()).isEqualTo(20);

    }

    @Test
    void getBuyerRanking_ShouldReturnARankingList(){
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
        TicketDTO ticketDTO3 = new TicketDTO(
                "T3",
                "G3",
                "b2",
                LocalDate.of(2025, 10, 1),
                LocalTime.of(10, 00, 00),
                15.0
        );

        when(ticketClient.getByBuyerId(buyer1.getBuyerId())).thenReturn(List.of(ticketDTO1, ticketDTO2));
        when(ticketClient.getByBuyerId(buyer2.getBuyerId())).thenReturn(List.of(ticketDTO3));

        //Act
        List<BuyerRankingDTO> list= buyerService.getBuyerRanking();

        //Assert
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getBuyerId()).isEqualTo("b1");
        assertThat(list.get(0).getTicketsCount()).isEqualTo(2);
        assertThat(list.get(1).getBuyerId()).isEqualTo("b2");
        assertThat(list.get(1).getTicketsCount()).isEqualTo(1);
    }

}
