package ru.practicum.ewm_service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.stats.StatsClientConnector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationDtoEnhancer {

    private final RequestRepository requestRepository;
    private final StatsClientConnector statsClient;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    public CompilationDto getEnhancedCompilationDto(Compilation compilation, List<Event> events) {
        if (events.isEmpty()) {
            return compilationMapper.toCompilationDto(compilation, Set.of());
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(
                        eventIds, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(CountRequests::getEventId, CountRequests::getCount));

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        Map<Long, Long> viewsMap = statsClient.getViews(
                start.minusHours(1),
                LocalDateTime.now().plusHours(1),
                eventIds.stream().map(id -> "/events/" + id).toList(),
                true);

        Set<EventShortDto> eventShortDtos = events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        requestsMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toSet());

        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }
}
