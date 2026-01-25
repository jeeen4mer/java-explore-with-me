package ru.practicum.ewm_service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserMapper;
import ru.practicum.ewm_service.user.UserRepository;
import ru.practicum.ewm_service.user.dto.NewUserRequest;
import ru.practicum.ewm_service.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        log.debug("Поиск пользователей: ids={}, from={}, size={}", ids, from, size);

        validatePagination(from, size);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (ids == null || ids.isEmpty()) {
            log.debug("Поиск ВСЕХ пользователей, страница {}", page);
            return repository.findAll(pageable).stream()
                    .map(mapper::userToDto)
                    .toList();
        } else {
            validateUserIds(ids);
            log.debug("Поиск пользователей по IDs: {}", ids);

            return repository.findAllById(ids, pageable).stream()
                    .map(mapper::userToDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        log.debug("Создание пользователя: email={}", newUserRequest.email());

        validateNewUserRequest(newUserRequest);

        if (repository.existsByEmail(newUserRequest.email())) {
            log.warn("Попытка создать пользователя с существующим email: {}", newUserRequest.email());
            throw new ConflictException("User with email " + newUserRequest.email() + " already exists");
        }

        User user = mapper.createUserDtoToUser(newUserRequest);
        user = repository.save(user);
        log.info("Создан пользователь с ID: {}", user.getId());

        return mapper.userToDto(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        log.debug("Удаление пользователя с ID: {}", userId);

        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (isAdminUser(user)) {
            throw new IllegalArgumentException("Cannot delete administrator user with id=" + userId);
        }

        repository.delete(user);
        log.info("Пользователь с ID {} удален", userId);
    }

    private void validatePagination(int from, int size) {
        if (from < 0) {
            throw new IllegalArgumentException("From parameter must be positive or zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter must be positive");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Size parameter must not exceed 100");
        }
    }

    private void validateUserIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            if (ids.size() > 100) {
                throw new IllegalArgumentException("Cannot request more than 100 users at once");
            }
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    throw new IllegalArgumentException("User ID must be positive");
                }
            }
        }
    }

    private void validateNewUserRequest(NewUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("User request cannot be null");
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!request.email().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isAdminUser(User user) {
        return user.getEmail().endsWith("@admin.com") || "admin".equals(user.getName());
    }

    @Transactional(readOnly = true)
    public UserDto findById(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        return mapper.userToDto(user);
    }
}