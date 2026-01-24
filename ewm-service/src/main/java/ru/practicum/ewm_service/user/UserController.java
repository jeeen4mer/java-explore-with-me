package ru.practicum.ewm_service.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.user.dto.NewUserRequest;
import ru.practicum.ewm_service.user.dto.UserDto;
import ru.practicum.ewm_service.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService service;
    private final UserValidator validator;

    @GetMapping
    public List<UserDto> findAll(@RequestParam(required = false) List<Long> ids,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение пользователей: ids = {}, from = {}, size = {}", ids, from, size);
        return service.findUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserRequest dto) {
        validator.validate(dto);
        log.info("Запрос на создание пользователя: name = '{}', email = '{}'", dto.name(), dto.email());
        return service.create(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        service.delete(userId);
    }
}
