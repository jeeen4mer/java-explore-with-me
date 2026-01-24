package ru.practicum.ewm_service.event.service.common;

import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.EventSortType;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEventsByFilters(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           boolean onlyAvailable,
                                           EventSortType sort,
                                           int from,
                                           int size,
                                           String ip);

    EventFullDto getEventById(long eventId, String ip);
}
