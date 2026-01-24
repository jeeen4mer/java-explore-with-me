package ru.practicum.ewm_service.event.validation;

import org.springframework.stereotype.Component;

@Component
public class EventValidator extends BaseEventValidator {

    public void validate(Validatable dto) {
        validateAnnotation(dto);
        validateEventDate(dto);
        validateTitle(dto);
        validateDescription(dto);
    }
}
