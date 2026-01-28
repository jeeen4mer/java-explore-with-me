package ru.practicum.ewm_service.event.dto;

import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm_service.event.model.Location;

import java.time.LocalDateTime;

public record UpdateEventUserRequest(String annotation,
                                     String description,
                                     LocalDateTime eventDate,
                                     Boolean paid,
                                     @PositiveOrZero Integer participantLimit,
                                     Boolean requestModeration,
                                     StateAction stateAction,
                                     String title,
                                     Long category,
                                     Location location) {


    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
