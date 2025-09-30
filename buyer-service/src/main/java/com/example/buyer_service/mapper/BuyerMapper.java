package com.example.buyer_service.mapper;


import com.example.buyer_service.dtos.BuyerDTO;
import com.example.buyer_service.models.Buyer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BuyerMapper {
    Buyer toEntity(BuyerDTO buyerDTO);
    BuyerDTO toDto(Buyer buyer);

    //Le dice a MapStruct: "si un campo en el DTO viene en null, no lo sobrescribas en la entidad".
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    //@MappingTarget
    //Le dice a MapStruct: "en lugar de crear un nuevo objeto, actualiza el que te paso".
    void updateFromDTO(BuyerDTO dto, @MappingTarget Buyer buyer);
}
