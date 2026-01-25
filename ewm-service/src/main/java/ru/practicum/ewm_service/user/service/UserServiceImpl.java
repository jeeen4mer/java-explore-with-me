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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        validatePagination(from, size);

        validateUserIds(ids);

        if (ids == null || ids.isEmpty()) {
            ids = null;
        }

        return repository.findAllById(ids, PageRequest.of(from / size, size)).stream()
                .map(mapper::userToDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        validateNewUserRequest(newUserRequest);

        if (repository.existsByEmail(newUserRequest.email())) {
            throw new IllegalArgumentException("User with email " + newUserRequest.email() + " already exists");
        }

        User user = mapper.createUserDtoToUser(newUserRequest);
        user = repository.save(user);
        return mapper.userToDto(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (isAdminUser(user)) {
            throw new IllegalArgumentException("Cannot delete administrator user with id=" + userId);
        }

        repository.delete(user);

        if (repository.existsById(userId)) {
            throw new IllegalStateException("Failed to delete user with id=" + userId);
        }
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
        if (ids != null) {
            if (ids.size() > 100) {
                throw new IllegalArgumentException("Cannot request more than 100 users at once");
            }
            for (Long id : ids) {
                if (id != null && id <= 0) {
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
        if (request.email().length() > 254) {
            throw new IllegalArgumentException("Email cannot exceed 254 characters");
        }
        if (request.name().length() > 250) {
            throw new IllegalArgumentException("Name cannot exceed 250 characters");
        }
        if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isAdminUser(User user) {
        return user.getEmail().endsWith("@admin.com") || "admin".equals(user.getName());
    }

    @Override
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