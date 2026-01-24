package ru.practicum.ewm_service.event.dto;

import ru.practicum.ewm_service.event.model.Location;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(String annotation,
                                      String description,
                                      LocalDateTime eventDate,
                                      Boolean paid,
                                      Integer participantLimit,
                                      Boolean requestModeration,
                                      StateAction stateAction,
                                      String title,
                                      Long category,
                                      Location location) {
    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }
}
