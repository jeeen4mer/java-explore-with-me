package ru.practicum.ewm_service.user;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.exception.ValidationException;
import ru.practicum.ewm_service.user.dto.NewUserRequest;

@Component
public class UserValidator {

    public void validate(NewUserRequest request) {
        validateName(request.name());
        validateEmail(request.email());
    }

    private void validateName(String name) {
        if (name.length() < 2 || name.length() > 250)
            throw new ValidationException("Name length should be from 2 to 250");
    }

    private void validateEmail(String email) {
        if (email.length() < 6 || email.length() > 254)
            throw new ValidationException("Name length should be from 2 to 250");
    }
}
