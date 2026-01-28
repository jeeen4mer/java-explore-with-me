package ru.practicum.ewm_service.event.service.admin;

import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(List<Long> users,
                                 List<EventState> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 int from,
                                 int size
    );

    EventFullDto update(long eventId, UpdateEventAdminRequest request);
}
