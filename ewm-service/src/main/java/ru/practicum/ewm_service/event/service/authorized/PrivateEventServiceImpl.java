package ru.practicum.ewm_service.event.service.authorized;

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
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.dto.NewEventDto;
import ru.practicum.ewm_service.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.event.validation.UpdateEventUserRequestValidator;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.RequestMapper;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm_service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.model.RequestDecision;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.request.validation.RequestStatusValidator;
import ru.practicum.ewm_service.stats.StatsClientConnector;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClientConnector statsClient;
    private final UpdateEventUserRequestValidator updateValidator;
    private final RequestStatusValidator statusValidator;


    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto dto) {
        User initiator = getUser(userId);
        Category category = getCategory(dto.category());
        Event event = eventRepository.save(eventMapper.fromNewEventDto(dto, category, initiator));
        int confirmedRequests = 0;
        long views = 0;
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public List<EventShortDto> getAllUserEvents(long userId, int from, int size) {
        List<Event> events = eventRepository.findByInitiatorIdAndIdGreaterThanEqualOrderById(
                        userId, from, PageRequest.of(0, size))
                .getContent();

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(ids, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(CountRequests::getEventId, CountRequests::getCount));

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        Map<Long, Long> viewsMap = statsClient.getViews(
                start,
                LocalDateTime.now().plusHours(1),
                ids.stream().map(id -> "/events/" + id).toList(),
                true);

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        requestsMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    public EventFullDto getFullEventById(long userId, long eventId) {
        Event event = getEvent(userId, eventId);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        long views;
        if (event.getPublishedOn() == null) {
            views = 0;
        } else {
            views = statsClient.getViews(event.getPublishedOn(), LocalDateTime.now().plusHours(1), List.of("/events/" + eventId), true)
                    .values()
                    .stream()
                    .mapToLong(Long::longValue)
                    .sum();
        }
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest request) {
        Event event = getEvent(userId, eventId);
        updateValidator.validate(request);
        updateValidator.validateEventState(event);
        updateValidator.validateRequest(request);
        Category category = request.category() == null ? event.getCategory() : categoryRepository.findById(request.category())
                .orElseThrow(() -> new NotFoundException("Category with id=" + request.category() + "was not found"));
        EventState state = EventState.PENDING;
        if (request.stateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
            state = EventState.CANCELED;
        }
        eventMapper.updateEventFromUserRequest(event, request, category, state);
        int confirmedRequests = 0;
        long views = 0;
        return eventMapper.toEventFullDto(eventRepository.save(event), confirmedRequests, views);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        Event event = getEvent(userId, eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeEventRequestsStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        Event event = getEvent(userId, eventId);
        List<Long> ids = request.requestIds();
        List<ParticipationRequest> requests = requestRepository.findAllById(ids);
        statusValidator.validate(requests);
        if (request.status().equals(RequestDecision.REJECTED)) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            requestRepository.flush();
            return new EventRequestStatusUpdateResult(
                    List.of(),
                    requests.stream()
                            .map(requestMapper::toRequestDto)
                            .toList()
            );
        }

        int requestsConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int remainedLimit = event.getParticipantLimit() - requestsConfirmed;
        if (remainedLimit <= 0) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            requestRepository.flush();
            throw new ConditionsNotMetException("The participant limit has been reached");
        }
        if (remainedLimit >= requests.size()) {
            requests.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            if (remainedLimit == requests.size())
                requestRepository.cancelAllRequests(eventId, RequestStatus.REJECTED, RequestStatus.PENDING);
            requestRepository.flush();
            return new EventRequestStatusUpdateResult(
                    requests.stream().map(requestMapper::toRequestDto).toList(),
                    List.of()
            );
        }

        List<Long> idsToConfirm = new ArrayList<>(ids.subList(0, remainedLimit));
        List<Long> idsToReject = new ArrayList<>(ids.subList(remainedLimit, ids.size()));

        List<ParticipationRequest> requestsToConfirm = requests.stream()
                .filter(r -> idsToConfirm.contains(r.getId()))
                .toList();
        requestsToConfirm.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));

        List<ParticipationRequest> requestsToReject = requests.stream()
                .filter(r -> idsToReject.contains(r.getId()))
                .toList();
        requestsToReject.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        requestRepository.flush();

        return new EventRequestStatusUpdateResult(
                requestsToConfirm.stream()
                        .map(requestMapper::toRequestDto)
                        .toList(),
                requestsToReject.stream()
                        .map(requestMapper::toRequestDto)
                        .toList()
        );
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEvent(long userId, long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + "was not found"));
    }

    private Category getCategory(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }
}
