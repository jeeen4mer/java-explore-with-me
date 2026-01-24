package ru.practicum.ewm_service.event.dto;

import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(Long id,
                            String annotation,
                            int confirmedRequests,
                            LocalDateTime eventDate,
                            boolean paid,
                            String title,
                            long views,
                            CategoryDto category,
                            UserShortDto initiator) {
}
