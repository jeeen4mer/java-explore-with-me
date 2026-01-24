package ru.practicum.ewm_service.category.dto;

import jakarta.validation.constraints.NotBlank;

public record NewCategoryDto(@NotBlank String name) {
}
