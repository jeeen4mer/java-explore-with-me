package ru.practicum.ewm_service.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm_service.event.model.Location;
import ru.practicum.ewm_service.event.validation.Validatable;

import java.time.LocalDateTime;

public record NewEventDto(@NotBlank String annotation,
                          @NotBlank String description,
                          @NotNull LocalDateTime eventDate,
                          boolean paid,
                          @PositiveOrZero int participantLimit,
                          Boolean requestModeration,
                          @NotBlank String title,
                          long category,
                          @NotNull Location location) implements Validatable {

    public NewEventDto {
        if (requestModeration == null) {
            requestModeration = true;
        }
    }

    @Override
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAnnotation() {
        return annotation;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
