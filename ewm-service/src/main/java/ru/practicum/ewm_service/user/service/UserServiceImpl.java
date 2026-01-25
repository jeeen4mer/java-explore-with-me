package ru.practicum.ewm_service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserMapper;
import ru.practicum.ewm_service.user.UserRepository;
import ru.practicum.ewm_service.user.dto.NewUserRequest;
import ru.practicum.ewm_service.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) ids = null;
        return repository.findAllById(ids, PageRequest.of(from / size, size)).stream()
                .map(mapper::userToDto)
                .toList();
    }

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        User user = mapper.createUserDtoToUser(newUserRequest);
        user = repository.save(user);
        return mapper.userToDto(user);
    }

    @Override
    public void delete(long userId) {
        if (!repository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        repository.deleteById(userId);
    }
}
