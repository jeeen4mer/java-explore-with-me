package ru.practicum.ewm_service.compilation.dto;

import ru.practicum.ewm_service.event.dto.EventShortDto;

import java.util.Set;

public record CompilationDto(Set<EventShortDto> events, long id, boolean pinned, String title) {
}
