package ru.practicum.ewm_service.compilation.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record NewCompilationDto(Set<Long> events, boolean pinned, @NotBlank String title) {
}
