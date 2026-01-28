package ru.practicum.ewm_service.user.dto;

public record UserDto(long id, String email, String name, boolean followsProhibited) {
}
