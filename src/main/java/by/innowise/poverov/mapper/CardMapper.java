package by.innowise.poverov.mapper;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.entity.Card;
import by.innowise.poverov.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "userId", source = "user.id")
    CardReadDto toCardReadDto(Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    Card toCard(CardWriteDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId")
    void updateCardFromDto(CardWriteDto dto, @MappingTarget Card card);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder()
                .id(userId)
                .build();

    }
}
