package ru.practicum.ewm_service.user;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.exception.ValidationException;
import ru.practicum.ewm_service.user.dto.NewUserRequest;

@Component
public class UserValidator {

    public void validate(NewUserRequest request) {
        if (request == null) {
            throw new ValidationException("User request cannot be null");
        }

        validateName(request.name());
        validateEmail(request.email());
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 250) {
            throw new ValidationException("Name length should be from 2 to 250 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (email.length() < 6 || email.length() > 254) {
            throw new ValidationException("Email length should be from 6 to 254 characters");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Invalid email format");
        }
    }
}