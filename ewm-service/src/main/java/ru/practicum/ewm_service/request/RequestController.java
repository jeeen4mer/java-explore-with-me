package ru.practicum.ewm_service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final ParticipationRequestService service;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable long userId) {
        log.info("Запрос от пользователя с id = {} на получение своих заявок", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @RequestParam long eventId) {
        log.info("Запрос от пользователя с id = {} на создание заявки на участие в событии с id = {}", userId, eventId);
        return service.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId,
                                          @PathVariable long requestId) {
        log.info("Запрос от пользователя с id = {} на отмену заявки с id = {}", userId, requestId);
        return service.cancel(userId, requestId);
    }
}
