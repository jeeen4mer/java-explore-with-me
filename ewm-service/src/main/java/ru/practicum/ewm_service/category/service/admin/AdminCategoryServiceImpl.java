package ru.practicum.ewm_service.category.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.category.CategoryMapper;
import ru.practicum.ewm_service.category.CategoryRepository;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;
import ru.practicum.ewm_service.exception.NotFoundException;

@RequiredArgsConstructor
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        return mapper.categoryToDto(repository.save(mapper.newCategoryDtotoCategory(dto)));
    }

    @Override
    public CategoryDto update(NewCategoryDto dto, long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        category.setName(dto.name());
        return mapper.categoryToDto(repository.save(category));
    }

    @Override
    public void delete(long id) {
        if (!repository.existsById(id))
            throw new NotFoundException("Category with id=" + id + " was not found");
        repository.deleteById(id);
    }
}
