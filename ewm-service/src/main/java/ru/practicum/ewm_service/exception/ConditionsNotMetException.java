package ru.practicum.ewm_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConditionsNotMetException extends RuntimeException {

    public ConditionsNotMetException(String field, String error, String value) {
        super("Field: " + field + ". Error: " + error + ". Value: " + value);
    }

    public ConditionsNotMetException(String message) {
        super(message);
    }
}
