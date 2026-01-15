package ru.practicum.stats_server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HitServiceImplIntegrationTest {

    @Autowired
    private HitService service;

    @Autowired
    private HitRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        LocalDateTime now = LocalDateTime.now();
        HitDto hit1 = new HitDto("app1", "/api/test", "192.168.1.1", now.minusMinutes(10));
        HitDto hit2 = new HitDto("app1", "/api/test", "192.168.1.2", now.minusMinutes(5));

        service.save(hit1);
        service.save(hit2);

        List<ResponseStatsDto> stats = service.find(now.minusHours(1), now.plusHours(1), List.of("/api/test"), false);

        assertEquals(1, stats.size());
        ResponseStatsDto dto = stats.get(0);
        assertEquals("app1", dto.getApp());
        assertEquals("/api/test", dto.getUri());
        assertEquals(2L, dto.getHits());

        List<ResponseStatsDto> uniqueStats = service.find(now.minusHours(1), now.plusHours(1), List.of("/api/test"), true);
        assertEquals(1, uniqueStats.size());
        assertEquals(2L, uniqueStats.get(0).getHits());
    }
}
