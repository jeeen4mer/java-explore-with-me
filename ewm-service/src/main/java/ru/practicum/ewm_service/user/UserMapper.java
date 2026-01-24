package ru.practicum.ewm_service.user;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm_service.user.dto.NewUserRequest;
import ru.practicum.ewm_service.user.dto.UserDto;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User createUserDtoToUser(NewUserRequest dto);

    UserDto userToDto(User user);

}
