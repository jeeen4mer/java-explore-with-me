package ru.practicum.stats_server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class HitRepositoryTest {

    @Autowired
    private HitRepository hitRepository;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        hitRepository.deleteAll();

        now = LocalDateTime.now();

        hitRepository.save(new Hit(null, "app1", "/api/test", "192.168.1.1", now.minusHours(1)));
        hitRepository.save(new Hit(null, "app1", "/api/test", "192.168.1.2", now.minusMinutes(30)));
        hitRepository.save(new Hit(null, "app2", "/api/other", "192.168.1.1", now.minusMinutes(10)));
    }

    @Test
    void testGetStats_allHits() {
        List<ResponseStatsDto> stats = hitRepository.getStats(
                now.minusDays(1),
                now.plusDays(1),
                null,
                false
        );

        assertEquals(2, stats.size());

        ResponseStatsDto first = stats.get(0);
        assertEquals("app1", first.getApp());
        assertEquals("/api/test", first.getUri());
        assertEquals(2L, first.getHits());

        ResponseStatsDto second = stats.get(1);
        assertEquals("app2", second.getApp());
        assertEquals("/api/other", second.getUri());
        assertEquals(1L, second.getHits());
    }

    @Test
    void testGetStats_uniqueHits() {
        List<ResponseStatsDto> stats = hitRepository.getStats(
                now.minusDays(1),
                now.plusDays(1),
                null,
                true
        );

        assertEquals(2, stats.size());

        ResponseStatsDto first = stats.get(0);
        assertEquals(2L, first.getHits());

        ResponseStatsDto second = stats.get(1);
        assertEquals(1L, second.getHits());
    }

    @Test
    void testGetStats_filterByUris() {
        List<ResponseStatsDto> stats = hitRepository.getStats(
                now.minusDays(1),
                now.plusDays(1),
                List.of("/api/test"),
                false
        );

        assertEquals(1, stats.size());
        assertEquals("/api/test", stats.get(0).getUri());
    }
}

