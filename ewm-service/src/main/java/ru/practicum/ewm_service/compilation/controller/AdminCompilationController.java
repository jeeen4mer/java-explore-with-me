package ru.practicum.ewm_service.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.compilation.CompilationValidator;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm_service.compilation.service.admin.AdminCompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationValidator validator;
    private final AdminCompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Admin запрос на добавление новой подборки с названием = {}", dto.title());
        validator.validateTitle(dto.title());
        return service.addNewCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Admin запрос на удаление подборки с id = {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable long compId, @RequestBody UpdateCompilationRequest request) {
        log.info("Admin запрос на обновление подборки с id = {}", compId);
        validator.validateTitle(request.title());
        return service.updateCompilation(request, compId);
    }

}
