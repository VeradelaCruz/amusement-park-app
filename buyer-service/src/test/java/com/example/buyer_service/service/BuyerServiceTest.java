package com.example.buyer_service.service;

import com.example.buyer_service.client.TicketClient;
import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.dtos.BuyerWithAmount;
import com.example.buyer_service.dtos.TicketDTO;
import com.example.buyer_service.exception.BuyerNotFoundException;
import com.example.buyer_service.mapper.BuyerMapper;
import com.example.buyer_service.models.Buyer;
import com.example.buyer_service.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/*
Pruebas unitarias:
-Se testea solo la lógica de BuyerService,
sin levantar Spring ni conectar a la base de datos real.
Para dependencias (como repositorios o Feign clients),
se usan mocks (@Mock) y se inyectan en la clase con @InjectMocks.
 */

//Activa Mockito en JUnit 5 y maneja automáticamente la inicialización de mocks.
@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {
    //Crea un objeto simulado (mock) de una dependencia.
    @Mock
    public BuyerRepository buyerRepository;

    @Mock
    private BuyerMapper buyerMapper;

    @Mock
    private TicketClient ticketClient;

    //Crea la clase bajo prueba e inyecta los mocks en ella.
    @InjectMocks
    private BuyerService buyerService;



    //Arrange:
    private Buyer buyer1;
    private Buyer buyer2;
    private BuyerDTO buyerDTO;

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

        buyerDTO = new BuyerDTO();
        buyerDTO.setFirstName("Alice");
        buyerDTO.setLastName("Smith");
        buyerDTO.setEmail("alice12@example.com");
        buyerDTO.setPhone("+34123456789");
        buyerDTO.setDocumentId("DOC12345");

    }

    @Test
    //Para el nombre del test:
    //[NombreDelMétodoQueSePrueba]_[Condición]_[ResultadoEsperado]
    void createBuyer_WhenValidInput_ShouldReturnBuyer() {
        // Arrange
        when(buyerRepository.save(buyer1)).thenReturn(buyer1);

        // Act
        Buyer result = buyerService.createBuyer(buyer1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBuyerId()).isEqualTo("b1");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");

        verify(buyerRepository, times(1)).save(buyer1);
    }

    @Test
    void findById_WhenExists_ShouldReturnBuyer(){
        //Arrange:
        when(buyerRepository.findById(buyer1.getBuyerId())).thenReturn(Optional.of(buyer1));

        //Act
        Buyer buyerResult= buyerService.findById(buyer1.getBuyerId());

        //Assert
        assertThat(buyerResult).isNotNull();
        assertThat(buyerResult.getBuyerId()).isEqualTo(buyer1.getBuyerId());

        verify(buyerRepository, times(1)).findById(buyer1.getBuyerId());

    }

    @Test
    void findById_WhenIsNotPresent_ShouldReturnException(){
        //Arrange
        String id= "999L";
        when(buyerRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert: verificamos que se lanza la excepción
        assertThatThrownBy(() -> buyerService.findById(id))
                .isInstanceOf(BuyerNotFoundException.class)
                .hasMessageContaining("Buyer with id: "+ id + " not found.");

        verify(buyerRepository, times(1)).findById(id);
    }

    @Test
    void findAll_ShouldReturnAList(){
        //Arrange:
        when(buyerRepository.findAll()).thenReturn(List.of(buyer1, buyer2));

        //Act
        List<Buyer> buyers= buyerService.findAll();

        //Assert
        assertThat(buyers).hasSize(2);
        assertThat(buyers).contains(buyer1, buyer2);
        verify(buyerRepository, times(1)).findAll();
    }

    @Test
    void removeBuyer_WhenIsPresent_ShouldReturnVoid(){
        //Arrange
        when(buyerRepository.findById(buyer1.getBuyerId())).thenReturn(Optional.of(buyer1));
        doNothing().when(buyerRepository).deleteById(buyer1.getBuyerId());

        //Act
        buyerService.removeBuyer(buyer1.getBuyerId());

        //Assert
        verify(buyerRepository, times(1)).deleteById(buyer1.getBuyerId());
    }

    @Test
    void removeBuyer_WhenIsNotPresent_ShouldReturnException(){
        //Arrange
        String id= "999L";
        when(buyerRepository.findById(id)).thenReturn(Optional.empty());

        //Act + assert
        assertThatThrownBy(()-> buyerService.removeBuyer(id))
                .isInstanceOf(BuyerNotFoundException.class)
                .hasMessageContaining("Buyer with id: "+ id + " not found.");

        verify(buyerRepository, times(1)).findById(id);
    }

    @Test
    void changeBuyer_ShouldReturnDTO() {
        // Arrange
        when(buyerRepository.findById("b1")).thenReturn(Optional.of(buyer1));
        when(buyerRepository.save(any(Buyer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Simulamos mapper: el DTO actualizado que debería devolver
        BuyerDTO expectedDTO = new BuyerDTO(
                "Alice",
                "Smith",
                "alice12@example.com", // el email actualizado
                "+34123456789",
                "DOC12345"
        );
        when(buyerMapper.toDto(any(Buyer.class))).thenReturn(expectedDTO);

        // Act
        BuyerDTO result = buyerService.changeBuyer(buyer1.getBuyerId(), buyerDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice12@example.com");
        assertThat(result.getFirstName()).isEqualTo("Alice");

        verify(buyerRepository, times(1)).findById("b1");
        verify(buyerRepository, times(1)).save(any(Buyer.class));
        verify(buyerMapper, times(1)).toDto(any(Buyer.class));
    }

    @Test
    void findBuyerWithTotalAmount_ShouldReturnADTO() {
        //Arrange
        when(buyerRepository.findById(buyer1.getBuyerId())).thenReturn(Optional.of(buyer1));

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

        when(ticketClient.getByBuyerId(buyer1.getBuyerId())).thenReturn(List.of(ticketDTO1,ticketDTO2));

        //Act
        BuyerWithAmount buyerWithAmount= buyerService.findBuyerWithTotalAmount(buyer1.getBuyerId());

        //Assert
        assertThat(buyerWithAmount).isNotNull();
        assertThat(buyerWithAmount.getTotalAmountSpent()).isEqualTo(20);
        assertThat(buyerWithAmount.getBuyerId()).isEqualTo("b1");



    }
}
