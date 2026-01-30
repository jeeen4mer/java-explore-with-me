package ru.practicum.ewm_service.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.stats.StatsClientConnector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventsFilterHandler {
    private final RequestRepository requestRepository;
    private final StatsClientConnector statsClient;
    private final EventMapper eventMapper;
    private static final String APP_NAME = "ewm-service";
    private static final String BASE_URI = "/events";

    public List<EventShortDto> handleFiltering(List<Event> events, boolean onlyAvailable, EventSortType sort,
                                               int from, int size, String ip) {
        if (events.isEmpty())
            return List.of();

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(ids, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(CountRequests::getEventId, CountRequests::getCount));

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        Map<Long, Long> viewsMap = statsClient.getViews(
                start,
                LocalDateTime.now(),
                ids.stream().map(id -> "/events/" + id).toList(),
                true);

        List<EventShortDto> dtos = new ArrayList<>(events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        requestsMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .toList());

        if (onlyAvailable) {
            Map<Long, Integer> requestsLimit = events.stream()
                    .collect(Collectors.toMap(Event::getId, Event::getParticipantLimit));

            dtos = dtos.stream()
                    .filter(event -> event.confirmedRequests() < requestsLimit.get(event.id()))
                    .collect(Collectors.toList());
        }

        if (sort == EventSortType.VIEWS)
            dtos.sort(Comparator.comparing(EventShortDto::views).reversed());

        statsClient.saveHit(APP_NAME, BASE_URI, ip, LocalDateTime.now());

        if (dtos.size() > size + from) {
            return dtos.subList(from, from + size);
        } else if (dtos.size() >= from) {
            return dtos.subList(from, dtos.size());
        }
        return dtos;
    }
}