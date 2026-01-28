package ru.practicum.ewm_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.event.service.admin.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventController {
    private final AdminEventService service;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Admin запрос от админа на получение событий с фильтрами: users = {}, states = {}, categories = {}, " +
                        "rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId, @RequestBody UpdateEventAdminRequest request) {
        log.info("Admin запрос на изменение события id={}, state={}, paid={}, limit={}, cat={}",
                eventId, request.stateAction(), request.paid(), request.participantLimit(), request.category());
        return service.update(eventId, request);
    }
}
