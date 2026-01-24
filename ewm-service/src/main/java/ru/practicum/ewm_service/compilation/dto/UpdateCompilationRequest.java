package ru.practicum.ewm_service.compilation.dto;

import java.util.Set;

public record UpdateCompilationRequest(Set<Long> events, Boolean pinned, String title) {
}
