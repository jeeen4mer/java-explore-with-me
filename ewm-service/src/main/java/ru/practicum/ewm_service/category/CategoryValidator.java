package ru.practicum.ewm_service.category;

import org.springframework.stereotype.Component;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;
import ru.practicum.ewm_service.exception.ValidationException;

@Component
public class CategoryValidator {
    public void validate(NewCategoryDto dto) {
        if (dto.name().length() > 50)
            throw new ValidationException("Category name max length = 50");
    }
}
