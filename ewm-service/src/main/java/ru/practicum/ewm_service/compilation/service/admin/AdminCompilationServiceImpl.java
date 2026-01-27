package ru.practicum.ewm_service.compilation.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm_service.compilation.Compilation;
import ru.practicum.ewm_service.compilation.CompilationDtoEnhancer;
import ru.practicum.ewm_service.compilation.CompilationMapper;
import ru.practicum.ewm_service.compilation.CompilationRepository;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm_service.event.dal.EventRepository;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationDtoEnhancer compilationDtoEnhancer;

    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto compilationDto) {
        List<Event> events;
        if (compilationDto.events() == null) {
            events = List.of();
        } else {
            events = eventRepository.findAllById(compilationDto.events());
        }
        Compilation compilation = compilationRepository.save(compilationMapper.fromNewCompilationDto(compilationDto,
                new HashSet<>(events)));
        return compilationDtoEnhancer.getEnhancedCompilationDto(compilation, events);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + compilationId + " not found"));

        if (!compilation.getEvents().isEmpty()) {
            throw new ConflictException("Cannot delete compilation with id=" + compilationId +
                    " because it still contains events.");
        }

        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest request, long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + compId + " not found"));
        List<Event> events = new ArrayList<>();
        if (request.events() != null) {
            events = eventRepository.findAllById(request.events());
        }
        if (events.isEmpty()) {
            compilationMapper.updateCompilationFromRequest(compilation, request, null);
            return compilationMapper.toCompilationDto(compilation, null);
        }
        compilationMapper.updateCompilationFromRequest(compilation, request, new HashSet<>(events));
        return compilationDtoEnhancer.getEnhancedCompilationDto(compilation, events);
    }
}
