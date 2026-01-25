package ru.practicum.ewm_service.compilation.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.compilation.Compilation;
import ru.practicum.ewm_service.compilation.CompilationDtoEnhancer;
import ru.practicum.ewm_service.compilation.CompilationMapper;
import ru.practicum.ewm_service.compilation.CompilationRepository;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.stats.StatsClientConnector;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final CompilationDtoEnhancer compilationDtoEnhancer;
    private final StatsClientConnector statsClient;

    @Override
    public List<CompilationDto> findByFilters(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAllWithEvents(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinnedWithEvents(pinned, pageable).toList();
        }

        List<Event> allEvents = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .toList();

        if (allEvents.isEmpty()) {
            return compilations.stream()
                    .map(compilation -> compilationMapper.toCompilationDto(compilation, Set.of()))
                    .toList();
        }

        List<Long> eventIds = allEvents.stream()
                .map(Event::getId)
                .distinct()
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(
                        eventIds, RequestStatus.CONFIRMED).stream()
                .collect(Collectors.toMap(CountRequests::getEventId, CountRequests::getCount));

        LocalDateTime start = allEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        log.info("1 = {}, 2 = {}, 3 = {}, 4 = {}",
                start.minusDays(10),
                LocalDateTime.now().plusDays(10),
                eventIds.stream().map(id -> "/events/" + id).toList(),
                true);

        Map<Long, Long> viewsMap = statsClient.getViews(
                start.minusDays(10),
                LocalDateTime.now().plusDays(10),
                eventIds.stream().map(id -> "/events/" + id).toList(),
                true);

        Map<Long, EventShortDto> eventDtoMap = allEvents.stream()
                .collect(Collectors.toMap(Event::getId,
                        event -> eventMapper.toEventShortDto(
                                event,
                                requestsMap.getOrDefault(event.getId(), 0),
                                viewsMap.getOrDefault(event.getId(), 0L))
                ));

        return compilations.stream()
                .map(compilation -> compilationMapper.toCompilationDto(
                        compilation,
                        compilation.getEvents().stream()
                                .map(event -> eventDtoMap.get(event.getId()))
                                .collect(Collectors.toSet())))
                .toList();
    }

    @Override
    public CompilationDto findById(long compId) {
        Compilation compilation = compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + compId + " not found"));

        List<Event> events = new ArrayList<>(compilation.getEvents());
        return compilationDtoEnhancer.getEnhancedCompilationDto(compilation, events);
    }
}