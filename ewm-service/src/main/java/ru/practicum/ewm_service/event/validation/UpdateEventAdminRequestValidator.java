package ru.practicum.ewm_service.event.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;

@Component
public class UpdateEventAdminRequestValidator {
    public void validate(UpdateEventAdminRequest request) {
        validateAnnotation(request);
        validateEventDate(request);
        validateTitle(request);
        validateDescription(request);
    }

    private void validateTitle(UpdateEventAdminRequest request) {
        String title = request.title();
        if (title == null) return;
        if (title.length() > 120 || title.length() < 3)
            throw new ValidationException("длинна заголовка должна быть от 3 до 120 символов");
    }

    private void validateAnnotation(UpdateEventAdminRequest request) {
        String annotation = request.annotation();
        if (annotation == null) return;
        if (annotation.length() > 2000 || annotation.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 2000 символов");
    }

    private void validateDescription(UpdateEventAdminRequest request) {
        String description = request.description();
        if (description == null) return;
        if (description.length() > 7000 || description.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 7000 символов");
    }

    private void validateEventDate(UpdateEventAdminRequest request) {
        if (request.eventDate() == null) return;
        if (request.eventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ValidationException("Event begins in less than 1 hour");
    }

    public void validateEventState(Event event) {
        EventState state = event.getState();
        if (state.equals(EventState.PUBLISHED) || state.equals(EventState.CANCELED))
            throw new ConditionsNotMetException("Cannot patch the event. State is not: PENDING");
    }

    public void validateRequest(UpdateEventAdminRequest request) {
        if (request.eventDate() != null && request.eventDate().isBefore(LocalDateTime.now()))
            throw new ValidationException(request.eventDate() + "was in the past");

    }
}
