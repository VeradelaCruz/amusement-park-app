package com.example.games_service.mapper;

import com.example.games_service.dtos.GameDTO;
import com.example.games_service.models.Game;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

//Le dice a MapStruct que esta interfaz es un mapper (mapeador).
//componentModel = "spring" significa que MapStruct va a generar
// una clase de implementación automática de esta interfaz y la va a registrar como bean de Spring.
@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO toDTO(Game game);
    Game toEntity(GameDTO gameDTO);
    //Le dice a MapStruct: "si un campo en el DTO viene en null, no lo sobrescribas en la entidad".
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    //@MappingTarget Game game
    //Le dice a MapStruct: "en lugar de crear un nuevo objeto Game, actualiza el que te paso".
    void updateFromDTO(GameDTO dto, @MappingTarget Game game);
}
