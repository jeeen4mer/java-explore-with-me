package ru.practicum.ewm_service.event.validation;

import org.springframework.util.StringUtils;
import ru.practicum.ewm_service.exception.ValidationException;

import java.time.LocalDateTime;

public abstract class BaseEventValidator {

    public void validateEventDate(Validatable validatable) {
        LocalDateTime eventDate = validatable.getEventDate();
        if (eventDate == null || eventDate.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ValidationException("eventDate: начало события не может быть раньше, чем через 2 часа");
    }

    public void validateTitle(Validatable validatable) {
        String title = validatable.getTitle();
        if (!StringUtils.hasText(title) || title.length() > 120 || title.length() < 3)
            throw new ValidationException("длинна заголовка должна быть от 3 до 120 символов");
    }

    public void validateAnnotation(Validatable validatable) {
        String annotation = validatable.getAnnotation();
        if (!StringUtils.hasText(annotation) || annotation.length() > 2000 || annotation.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 2000 символов");
    }

    public void validateDescription(Validatable validatable) {
        String description = validatable.getDescription();
        if (!StringUtils.hasText(description) || description.length() > 7000 || description.length() < 20)
            throw new ValidationException("длинна аннотации должна быть от 20 до 7000 символов");
    }
}
