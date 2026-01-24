package ru.practicum.ewm_service.request.dto;

import ru.practicum.ewm_service.request.model.RequestStatus;

import java.time.LocalDateTime;

public record ParticipationRequestDto(LocalDateTime created,
                                      long event,
                                      long id,
                                      long requester,
                                      RequestStatus status) {
}
