package ru.practicum.ewm_service.category.service.admin;

import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto create(NewCategoryDto dto);

    CategoryDto update(NewCategoryDto dto, long id);

    void delete(long categoryId);
}
