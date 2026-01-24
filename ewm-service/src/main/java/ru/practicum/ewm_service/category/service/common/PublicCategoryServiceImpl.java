package ru.practicum.ewm_service.category.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.category.CategoryMapper;
import ru.practicum.ewm_service.category.CategoryRepository;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Page<Category> page = repository.findByIdGreaterThanEqualOrderById(from, PageRequest.of(0, size));
        return page.getContent().stream()
                .map(mapper::categoryToDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        return mapper.categoryToDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found")));
    }
}
