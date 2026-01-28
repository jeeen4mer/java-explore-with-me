package ru.practicum.ewm_service.compilation.service.common;

import ru.practicum.ewm_service.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> findByFilters(Boolean pinned, int from, int size);

    CompilationDto findById(long compId);
}
