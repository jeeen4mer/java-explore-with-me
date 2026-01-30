package ru.practicum.ewm_service.subscription.dto;

import java.time.LocalDateTime;

public record SubscriptionDto(long followerId, long contentMakerId, LocalDateTime created) {
}