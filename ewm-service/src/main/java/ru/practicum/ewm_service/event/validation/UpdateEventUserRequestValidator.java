package ru.practicum.ewm_service.event.validation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.ConditionsNotMetException;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;

@Component
public class UpdateEventUserRequestValidator {

    public void validate(UpdateEventUserRequest request) {
        validateAnnotation(request);
        validateEventDate(request);
        validateTitle(request);
        validateDescription(request);
    }

    private void validateEventDate(UpdateEventUserRequest request) {
        LocalDateTime eventDate = request.eventDate();
        if (eventDate == null) return;
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ValidationException("eventDate: начало события не может быть раньше, чем через 2 часа");
    }

    private void validateTitle(UpdateEventUserRequest request) {
        String title = request.title();
        if (title == null) return;
        if (title.length() > 120 || title.length() < 3)
            throw new ValidationException("длинна заголовка должна быть от 3 до 120 символов");
    }

    private void validateAnnotation(UpdateEventUserRequest request) {
        String annotation = request.annotation();
        if (annotation == null) return;
        if (annotation.length() > 2000 || annotation.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 2000 символов");
    }

    private void validateDescription(UpdateEventUserRequest request) {
        String description = request.description();
        if (description == null) return;
        if (description.length() > 7000 || description.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 7000 символов");
    }

    public void validateEventState(Event event) {
        if (event.getState().equals(EventState.PUBLISHED))
            throw new ConditionsNotMetException("Event has already been published");
    }

    public void validateRequest(UpdateEventUserRequest request) {
        if (request.eventDate() != null && request.eventDate().isBefore(LocalDateTime.now()))
            throw new ValidationException(request.eventDate() + "was in the past");
    }
}
