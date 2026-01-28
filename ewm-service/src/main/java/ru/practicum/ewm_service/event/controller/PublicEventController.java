package ru.practicum.ewm_service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
    public List<EventShortDto> getEventsByFilters(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) EventSortType sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        if (from < 0) {
            throw new ValidationException("Parameter 'from' must be >= 0");
        }
        if (size < 1 || size > 1000) {
            throw new ValidationException("Parameter 'size' must be between 1 and 1000");
        }

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Field: rangeEnd. Error: must not be before rangeStart.");
        }

        String clientIp = getClientIpAddress(request);

        log.info("Public запрос от {} на получение событий с фильтрами: text='{}', categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                clientIp, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return service.getEventsByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, clientIp);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable("id") long eventId,
            HttpServletRequest request
    ) {
        if (eventId <= 0) {
            throw new ValidationException("Event ID must be positive");
        }

        String clientIp = getClientIpAddress(request);
        log.info("Public запрос от {} на получение события с id = {}", clientIp, eventId);
        return service.getEventById(eventId, clientIp);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !xForwardedFor.equalsIgnoreCase("unknown")) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !xRealIp.equalsIgnoreCase("unknown")) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}