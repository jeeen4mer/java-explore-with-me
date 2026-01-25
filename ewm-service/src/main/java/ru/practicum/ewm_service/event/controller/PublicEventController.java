package ru.practicum.ewm_service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.event.service.common.PublicEventService;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final PublicEventService service;

    @GetMapping
    public List<EventShortDto> getEventsByFilters(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSortType sort,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  HttpServletRequest request
    ) {
        log.info("Public запрос от пользователя на получение событий с фильтрами: text = '{}', categories = {}, paid = {}, " +
                        "rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart))
            throw new ValidationException("RangeEnd can not be before rangeStart");
        return service.getEventsByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr());
    }


    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable(name = "id") long eventId, HttpServletRequest request) {
        log.info("Public запрос от пользователя на получение события с id = {}", eventId);
        return service.getEventById(eventId, request.getRemoteAddr());
    }
}
