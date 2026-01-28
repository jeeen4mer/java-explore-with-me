package ru.practicum.ewm_service.request.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.model.RequestStatus;

import java.util.List;

@Component
public class EventRequestStatusUpdateValidator {
    public void validate(List<ParticipationRequest> requests) {
        List<RequestStatus> statuses = requests.stream()
                .map(ParticipationRequest::getStatus)
                .toList();
        if (statuses.contains(RequestStatus.CONFIRMED) || statuses.contains(RequestStatus.CANCELED))
            throw new ConditionsNotMetException("Request must have status PENDING");

    }
}
