package ru.practicum.ewm_service.event;

import org.mapstruct.*;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.category.CategoryMapper;
import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserMapper;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "category", ignore = true)
    Event fromNewEventDto(NewEventDto dto, @Context Category category, @Context User initiator);

    @Mapping(target = "confirmedRequests", expression = "java(confirmedRequests)")
    @Mapping(target = "views", expression = "java(views)")
    EventFullDto toEventFullDto(Event event, int confirmedRequests, long views);

    @Mapping(target = "confirmedRequests", expression = "java(confirmedRequests)")
    @Mapping(target = "views", expression = "java(views)")
    EventShortDto toEventShortDto(Event event, int confirmedRequests, long views);

    @Mapping(target = "category", ignore = true)
    void updateEventFromUserRequest(@MappingTarget Event event, UpdateEventUserRequest request, @Context Category category,
                                    @Context EventState state);

    @Mapping(target = "category", ignore = true)
    void updateEventFromAdminRequest(@MappingTarget Event event, UpdateEventAdminRequest request, @Context Category category,
                                     @Context EventState state);

    @AfterMapping
    @SuppressWarnings("unused")
    default void enhanceEventForCreate(@MappingTarget Event event,
                                       @Context Category category,
                                       @Context User initiator) {
        if (category != null) event.setCategory(category);
        if (initiator != null) event.setInitiator(initiator);
    }

    @AfterMapping
    @SuppressWarnings("unused")
    default void enhanceEventForUpdate(@MappingTarget Event event,
                                       @Context Category category,
                                       @Context EventState state) {
        if (category != null) event.setCategory(category);
        if (state != null) event.setState(state);
    }
}
