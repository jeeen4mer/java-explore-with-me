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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationDtoEnhancer compilationDtoEnhancer;

    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.events() != null && !compilationDto.events().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(compilationDto.events()));
        }

        Compilation compilation = compilationRepository.save(
                compilationMapper.fromNewCompilationDto(compilationDto, events)
        );

        return compilationDtoEnhancer.getEnhancedCompilationDto(compilation, new ArrayList<>(events));
    }

    @Override
    @Transactional
    public void deleteCompilation(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + compilationId + " not found"));

        if (!compilation.getEvents().isEmpty()) {
            throw new ConflictException("Cannot delete compilation with id = " + compilationId +
                    " because it contains events. Remove events first.");
        }

        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest request, long compId) {
        Compilation compilation = compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + compId + " not found"));

        Set<Event> events = new HashSet<>();
        if (request.events() != null) {
            events = new HashSet<>(eventRepository.findAllById(request.events()));
        }

        compilationMapper.updateCompilationFromRequest(compilation, request, events);

        Compilation updatedCompilation = compilationRepository.save(compilation);

        return compilationDtoEnhancer.getEnhancedCompilationDto(
                updatedCompilation,
                new ArrayList<>(events.isEmpty() ? compilation.getEvents() : events)
        );
    }
}