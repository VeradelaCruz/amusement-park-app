package com.example.tickets_service.mapper;

import com.example.tickets_service.dtos.GameDTO;
import com.example.tickets_service.dtos.TicketDTO;
import com.example.tickets_service.models.Ticket;
import org.springframework.stereotype.Component;

//Marca la clase como un componente de Spring,
// lo que permite inyectarla en otros servicios usando @Autowired.
//Así no tenés que crear una instancia manualmente, Spring se encarga de manejarla.
@Component
public class TicketMapper {
    //transformar datos de la entidad Ticket y
    // GameDTO a un TicketDTO listo para devolver al cliente.
        public TicketDTO toDTO(Ticket ticket, GameDTO gameDTO) {
            //El patrón builder (gracias a Lombok @Builder) para crear el DTO de
            // manera limpia y legible.
            //Evita tener que llamar a un constructor con muchos parámetros.
            return TicketDTO.builder()
                    .ticketId(ticket.getTicketId())
                    .buyerId(ticket.getBuyerId())
                    .gameId(ticket.getGameId())
                    .date(ticket.getDate())
                    .time(ticket.getTime())
                    .price(ticket.getPrice())
                    .gameDTO(gameDTO)
                    //Termina la construcción del TicketDTO y devuelve la instancia lista para usar.
                    .build();
        }

}
