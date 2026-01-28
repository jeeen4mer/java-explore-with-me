package ru.practicum.ewm_service.event.dto;

import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventFullDto(Long id,
                           String annotation,
                           int confirmedRequests,
                           LocalDateTime createdOn,
                           String description,
                           LocalDateTime eventDate,
                           boolean paid,
                           int participantLimit,
                           LocalDateTime publishedOn,
                           boolean requestModeration,
                           EventState state,
                           String title,
                           long views,
                           CategoryDto category,
                           UserShortDto initiator,
                           Location location) {
}
