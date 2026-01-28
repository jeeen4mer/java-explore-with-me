package ru.practicum.ewm_service.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.event.dal.EventRepository;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.RequestMapper;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.request.validation.NewParticipationRequestValidator;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;
    private final NewParticipationRequestValidator validator;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("User with id=" + userId + " was not found");

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(mapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent())
            throw new ConditionsNotMetException("Запрос уже был создан ранее");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        int participantsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        validator.validate(userId, event, participantsCount);

        ParticipationRequest request = new ParticipationRequest(event, user);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0)
            request.setStatus(RequestStatus.CONFIRMED);
        return mapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request was not found"));
        if (request.getRequester().getId() != userId)
            throw new ConditionsNotMetException("Can cancel only your requests");
        if (!request.getStatus().equals(RequestStatus.PENDING))
            throw new ConditionsNotMetException("Can cancel only pending requests");
        request.setStatus(RequestStatus.CANCELED);
        return mapper.toRequestDto(request);
    }
}
