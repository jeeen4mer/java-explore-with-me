package ru.practicum.ewm_service.request.service;

import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);
}
