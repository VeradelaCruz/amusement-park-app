package com.example.tickets_service.mapper;

import com.example.tickets_service.dtos.GameDTO;
import com.example.tickets_service.dtos.TicketDTO;
import com.example.tickets_service.models.Ticket;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

//Marca la clase como un componente de Spring,
// lo que permite inyectarla en otros servicios usando @Autowired.
//Así no tenés que crear una instancia manualmente, Spring se encarga de manejarla.
@Mapper(componentModel = "spring")
public interface TicketMapper {
    // MapStruct se encarga de mapear los atributos con el mismo nombre
    TicketDTO toDTO(Ticket ticket);

    // metodo extra para meterle el GameDTO manualmente
    default TicketDTO toDTO(Ticket ticket, GameDTO gameDTO) {
        TicketDTO dto = toDTO(ticket); // esto lo genera MapStruct
        dto.setGameDTO(gameDTO);       // acá lo completás
        return dto;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        //@MappingTarget
        //Le dice a MapStruct: "en lugar de crear un nuevo objeto, actualiza el que te paso".
    void updateFromDTO(TicketDTO dto, @MappingTarget Ticket ticket);



}
