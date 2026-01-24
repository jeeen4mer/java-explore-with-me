package ru.practicum.ewm_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewUserRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String name) {
}
