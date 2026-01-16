package ru.practicum.stats_server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.model.Hit;
import ru.practicum.stats_server.model.HitMapper;
import ru.practicum.stats_server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HitServiceImplTest {

    private HitRepository repository;
    private HitMapper mapper;
    private HitServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(HitRepository.class);
        mapper = mock(HitMapper.class);
        service = new HitServiceImpl(repository, mapper);
    }

    @Test
    void testSave() {
        HitDto hitDto = new HitDto("app1", "/api/test", "127.0.0.1", LocalDateTime.now());
        Hit hitEntity = new Hit();

        when(mapper.fromDto(hitDto)).thenReturn(hitEntity);

        service.save(hitDto);

        ArgumentCaptor<Hit> captor = ArgumentCaptor.forClass(Hit.class);
        verify(repository).save(captor.capture());

        assertEquals(hitEntity, captor.getValue());
        verify(mapper).fromDto(hitDto);
    }

    @Test
    void testFind() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/api/test");
        boolean unique = true;

        List<ResponseStatsDto> mockStats = List.of(
                new ResponseStatsDto("app1", "/api/test", 5L)
        );

        when(repository.getStats(start, end, uris, unique)).thenReturn(mockStats);

        List<ResponseStatsDto> stats = service.find(start, end, uris, unique);

        assertEquals(mockStats, stats);
        verify(repository).getStats(start, end, uris, unique);
    }
}
