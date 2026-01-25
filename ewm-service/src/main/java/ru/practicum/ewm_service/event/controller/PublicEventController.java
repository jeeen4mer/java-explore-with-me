package ru.practicum.ewm_service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class PublicEventController {

    private final PublicEventService service;

    @GetMapping
    public List<EventShortDto> getEventsByFilters(
            @RequestParam(required = false)
            @Size(min = 1, max = 200, message = "Text must be between 1 and 200 characters")
            String text,

            @RequestParam(required = false)
            List<@Positive(message = "Category ID must be positive") Long> categories,

            @RequestParam(required = false)
            Boolean paid,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime rangeStart,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime rangeEnd,

            @RequestParam(required = false)
            Boolean onlyAvailable,

            @RequestParam(required = false)
            EventSortType sort,

            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "From parameter must be positive or zero")
            int from,

            @RequestParam(defaultValue = "10")
            @Positive(message = "Size parameter must be positive")
            @Max(value = 100, message = "Size parameter must not exceed 100")
            int size,

            HttpServletRequest request
    ) {
        log.info("Public запрос от пользователя на получение событий с фильтрами: text = '{}', categories = {}, paid = {}, " +
                        "rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        validateDateRange(rangeStart, rangeEnd);

        validateSearchText(text);

        validateCategories(categories);

        validatePagination(from, size);

        return service.getEventsByFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable != null ? onlyAvailable : false, sort, from, size,
                request.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(
            @PathVariable(name = "id")
            @Positive(message = "Event ID must be positive")
            long eventId,

            HttpServletRequest request
    ) {
        log.info("Public запрос от пользователя на получение события с id = {}", eventId);
        return service.getEventById(eventId, request.getRemoteAddr());
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("RangeEnd cannot be before rangeStart");
        }

        if (rangeStart != null && rangeStart.isBefore(LocalDateTime.now())) {
            throw new ValidationException("RangeStart cannot be in the past");
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.plusHours(1).isAfter(rangeEnd)) {
            throw new ValidationException("Date range must be at least 1 hour");
        }
    }

    private void validateSearchText(String text) {
        if (text != null) {
            if (text.trim().isEmpty()) {
                throw new ValidationException("Search text cannot be empty");
            }
            if (text.length() > 200) {
                throw new ValidationException("Search text cannot exceed 200 characters");
            }
        }
    }

    private void validateCategories(List<Long> categories) {
        if (categories != null) {
            if (categories.isEmpty()) {
                throw new ValidationException("Categories list cannot be empty");
            }
            if (categories.size() > 20) {
                throw new ValidationException("Cannot search by more than 20 categories");
            }
            if (categories.stream().anyMatch(id -> id <= 0)) {
                throw new ValidationException("Category ID must be positive");
            }
        }
    }

    private void validatePagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("From parameter must be positive or zero");
        }
        if (size <= 0) {
            throw new ValidationException("Size parameter must be positive");
        }
        if (size > 100) {
            throw new ValidationException("Size parameter must not exceed 100");
        }
    }
}