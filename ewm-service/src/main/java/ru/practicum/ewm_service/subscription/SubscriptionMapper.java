package ru.practicum.ewm_service.subscription;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm_service.subscription.dto.SubscriptionDto;
import ru.practicum.ewm_service.subscription.model.Subscription;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SubscriptionMapper {

    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "contentMakerId", source = "contentMaker.id")
    SubscriptionDto toSubscriptionDto(Subscription subscription);
}