package ru.practicum.ewm_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.service.common.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {

    private final PublicCompilationService service;

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable long compId) {
        log.info("Public запрос на получение подборки с id = {}", compId);
        return service.findById(compId);
    }

    @GetMapping
    public List<CompilationDto> findByFilters(@RequestParam(required = false) boolean pinned,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("Public запрос на получение подборок с параметрами pinned = {}, from = {}, size = {}",
                pinned, from, size);
        return service.findByFilters(pinned, from, size);
    }
}
