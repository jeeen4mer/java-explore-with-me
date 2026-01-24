package ru.practicum.ewm_service.category;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category newCategoryDtotoCategory(NewCategoryDto dto);

    CategoryDto categoryToDto(Category category);
}
