package ru.practicum.ewm_service.subscription.service;

import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.subscription.dto.SubscriptionDto;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {

    SubscriptionDto createSubscription(long followerId, long contentMakerId);

    void cancelSubscription(long followerId, long contentMakerId);

    List<UserShortDto> getFollowers(long userId, int from, int size);

    void changeFollowsPermission(long userId, boolean prohibit);

    List<EventShortDto> getSubsEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, boolean onlyAvailable, EventSortType sort,
                                      int from, int size, String ip, long userId);
}