package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.model.HitMapper;
import ru.practicum.stats_server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository repository;
    private final HitMapper mapper;

    @Override
    public void save(HitDto hitDto) {
        repository.save(mapper.fromDto(hitDto));
    }

    @Override
    public List<ResponseStatsDto> find(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return repository.getStats(start, end, uris, unique);
    }
}
