package ru.practicum.ewm_service.request.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;

@Component
public class NewParticipationRequestValidator {

    public void validate(long userId, Event event, int requestsCount) {
        validateInitiator(userId, event.getInitiator().getId());
        validateState(event.getState());
        validateLimit(event.getParticipantLimit(), requestsCount);
    }

    private void validateInitiator(long userId, long initiatorId) {
        if (userId == initiatorId)
            throw new ConditionsNotMetException(
                    "initiator",
                    "нельзя добавить запрос на участие в своём событии",
                    String.valueOf(initiatorId)
            );
    }

    private void validateState(EventState eventState) {
        if (!eventState.equals(EventState.PUBLISHED))
            throw new ConditionsNotMetException(
                    "state",
                    "нельзя участвовать в неопубликованном событии",
                    eventState.toString()
            );
    }


    private void validateLimit(int requestsLimit, int requestsCount) {
        if (requestsLimit == 0) return;

        if (requestsLimit <= requestsCount)
            throw new ConditionsNotMetException(
                    "participantsLimit",
                    "у события достигнут лимит запросов на участие",
                    String.valueOf(requestsLimit)
            );
    }
}