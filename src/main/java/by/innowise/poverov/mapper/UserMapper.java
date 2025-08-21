package by.innowise.poverov.mapper;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;
import by.innowise.poverov.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserReadDto toUserReadDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards",  ignore = true)
    User toUser(UserWriteDto userWriteDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards",  ignore = true)
    void updateUserFromDto(UserWriteDto userWriteDto,  @MappingTarget User user);
}
