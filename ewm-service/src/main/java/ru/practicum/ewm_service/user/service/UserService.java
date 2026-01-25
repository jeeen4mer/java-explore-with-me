package ru.practicum.ewm_service.user.service;

import ru.practicum.ewm_service.user.dto.NewUserRequest;
import ru.practicum.ewm_service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    UserDto create(NewUserRequest newUserRequest);

    void delete(long userId);
}
