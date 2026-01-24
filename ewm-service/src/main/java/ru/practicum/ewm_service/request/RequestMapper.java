package ru.practicum.ewm_service.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm_service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm_service.request.model.ParticipationRequest;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toRequestDto(ParticipationRequest request);
}
