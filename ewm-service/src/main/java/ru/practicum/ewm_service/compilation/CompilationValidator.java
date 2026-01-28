package ru.practicum.ewm_service.compilation;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.exception.ValidationException;

@Component
public class CompilationValidator {
    public void validateTitle(String title) {
        if (title != null && title.length() > 50)
            throw new ValidationException("Title max length = 50");
    }
}
