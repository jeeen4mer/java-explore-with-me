package ru.practicum.ewm_service.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm_service.compilation.dto.CompilationDto;
import ru.practicum.ewm_service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;

import java.util.Set;

@Mapper(componentModel = "spring",
        uses = EventMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    Compilation fromNewCompilationDto(NewCompilationDto dto, Set<Event> events);

    @Mapping(target = "events", source = "eventDtos")
    CompilationDto toCompilationDto(Compilation compilation, Set<EventShortDto> eventDtos);

    @Mapping(target = "events", source = "events")
    void updateCompilationFromRequest(@MappingTarget Compilation compilation, UpdateCompilationRequest request,
                                      Set<Event> events);
}
