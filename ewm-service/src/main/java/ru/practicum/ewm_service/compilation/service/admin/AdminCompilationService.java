package ru.practicum.ewm_service.compilation.service.admin;

import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto addNewCompilation(NewCompilationDto dto);

    void deleteCompilation(long compilationId);

    CompilationDto updateCompilation(UpdateCompilationRequest request, long compId);
}
