package ru.practicum.ewm_service.request.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.model.RequestStatus;

import java.util.List;

@Component
public class RequestStatusValidator {
    public void validate(List<ParticipationRequest> requests) {
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING)
                throw new ConditionsNotMetException("Request status should be PENDING");
        }
    }
}
