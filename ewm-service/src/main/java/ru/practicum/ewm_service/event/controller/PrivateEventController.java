package ru.practicum.ewm_service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.dto.NewEventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm_service.event.service.authorized.PrivateEventService;
import ru.practicum.ewm_service.event.validation.EventValidator;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {
    private final PrivateEventService service;
    private final EventValidator eventValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId, @Valid @RequestBody NewEventDto dto) {
        eventValidator.validate(dto);
        log.info("Private запрос от пользователя с id = {} на создание нового события", userId);
        return service.create(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getFullEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Private запрос от пользователя с id = {} на получение полной информации о событии с id = {}", userId, eventId);
        return service.getFullEventById(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getUsersEvents(@PathVariable long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.info("Private запрос от пользователя с id = {} на получение своих событий в количистве {}, пропустив первые {}",
                userId, size, from);
        return service.getAllUserEvents(userId, from, size);
    }


    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest request) {
        log.info("Private запрос от пользователя с id = {} на обновление события с id = {}", userId, eventId);
        return service.update(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable long userId,
                                                                       @PathVariable long eventId) {
        log.info("Private запрос от пользователя с id = {} на получение заявок на участие в событии с id = {}", userId, eventId);
        return service.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable long userId,
                                                              @PathVariable long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Private запрос от пользователя с id = {} на изменение статуса заявок на событие с id = {}", userId, eventId);
        return service.changeEventRequestsStatus(userId, eventId, request);
    }
}
