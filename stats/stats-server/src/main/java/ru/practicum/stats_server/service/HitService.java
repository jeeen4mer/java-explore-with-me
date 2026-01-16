package ru.practicum.stats_server.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    void save(HitDto hitDto);

    List<ResponseStatsDto> find(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
