package ru.practicum.ewm_service.event.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.category.CategoryRepository;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.event.dal.EventRepository;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.event.validation.EventValidator;
import ru.practicum.ewm_service.event.validation.UpdateEventAdminRequestValidator;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.stats.StatsClientConnector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClientConnector statsClient;
    private final EventMapper mapper;
    private final UpdateEventAdminRequestValidator requestValidator;
    private final EventValidator eventValidator;

    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        int from,
                                        int size) {
        if (users == null || users.isEmpty()) users = null;
        if (states == null || states.isEmpty()) states = null;
        if (categories == null || categories.isEmpty()) categories = null;
        List<Event> events = eventRepository.findAllByAdminFilters(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size)).getContent();
        if (events.isEmpty())
            return List.of();

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(eventIds, RequestStatus.CONFIRMED).stream()
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

        return events.stream()
                .map(event -> mapper.toEventFullDto(
                        event,
                        requestsMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto update(long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + "was not found"));
        eventValidator.validate(event);
        requestValidator.validate(request);
        requestValidator.validateEventState(event);
        requestValidator.validateRequest(request);
        Category category = request.category() == null ? event.getCategory() : categoryRepository.findById(request.category())
                .orElseThrow(() -> new NotFoundException("Category with id=" + request.category() + "was not found"));
        EventState newState = EventState.PENDING;
        if (request.stateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
            newState = EventState.PUBLISHED;
            event.setPublishedOn(LocalDateTime.now());
        } else if (request.stateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
            newState = EventState.CANCELED;
        }
        mapper.updateEventFromAdminRequest(event, request, category, newState);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        long views;
        if (event.getPublishedOn() == null) {
            views = 0;
        } else {
            views = statsClient.getViews(event.getPublishedOn(), LocalDateTime.now().plusHours(1),
                            List.of("/events/" + eventId), true)
                    .values()
                    .stream()
                    .mapToLong(Long::longValue)
                    .sum();
        }
        return mapper.toEventFullDto(event, confirmedRequests, views);
    }
}
