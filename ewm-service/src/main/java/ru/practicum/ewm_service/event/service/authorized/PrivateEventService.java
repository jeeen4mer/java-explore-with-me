package ru.practicum.ewm_service.event.service.authorized;

import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.dto.NewEventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto create(long userId, NewEventDto dto);

    List<EventShortDto> getAllUserEvents(long userId, int from, int size);

    EventFullDto getFullEventById(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult changeEventRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);
}
