package ru.practicum.ewm_service.request.dto;

import ru.practicum.ewm_service.request.model.RequestDecision;

import java.util.List;

public record EventRequestStatusUpdateRequest(List<Long> requestIds, RequestDecision status) {
}
