package ru.practicum.ewm_service.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.category.CategoryValidator;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.category.dto.NewCategoryDto;
import ru.practicum.ewm_service.category.service.admin.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final AdminCategoryService service;
    private final CategoryValidator validator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Admin запрос на создание категории: name = '{}'", dto.name());
        validator.validate(dto);
        return service.create(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId, @RequestBody @Valid NewCategoryDto dto) {
        log.info("Admin запрос на обновление категории с id = {}, name = '{}'", catId, dto.name());
        validator.validate(dto);
        return service.update(dto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        log.info("Admin запрос на удаление категории с id = {}", catId);
        service.delete(catId);
    }
}
