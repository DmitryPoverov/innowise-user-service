package by.innowise.poverov.mapper;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "userId", source = "user.id")
    CardReadDto toReadDto(Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Card toEntity(CardWriteDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(CardWriteDto dto, @MappingTarget Card card);
}
