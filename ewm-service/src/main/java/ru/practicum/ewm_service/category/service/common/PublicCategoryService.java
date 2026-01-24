package ru.practicum.ewm_service.category.service.common;

import ru.practicum.ewm_service.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
