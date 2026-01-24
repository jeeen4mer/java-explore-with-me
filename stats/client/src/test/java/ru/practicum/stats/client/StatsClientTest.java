package ru.practicum.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class StatsClientTest {

    private RestTemplate restTemplate;
    private StatsClient statsClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        statsClient = new StatsClient(restTemplate);
    }

    @Test
    void testSaveHit() {
        String app = "my-app";
        String uri = "/api/test";
        String ip = "127.0.0.1";
        LocalDateTime timestamp = LocalDateTime.now();

        when(restTemplate.postForEntity(anyString(), any(), eq(void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        statsClient.saveHit(app, uri, ip, timestamp);

        ArgumentCaptor<HitDto> captor = ArgumentCaptor.forClass(HitDto.class);
        verify(restTemplate).postForEntity(eq("/hit"), captor.capture(), eq(void.class));
        HitDto hitSent = captor.getValue();

        assertEquals(app, hitSent.getApp());
        assertEquals(uri, hitSent.getUri());
        assertEquals(ip, hitSent.getIp());
        assertEquals(timestamp, hitSent.getTimestamp());
    }

    @Test
    void testGetStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/api/test");
        boolean unique = true;

        List<ResponseStatsDto> mockResponse = List.of(
                new ResponseStatsDto("my-app", "/api/test", 5L)
        );

        ResponseEntity<List<ResponseStatsDto>> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<ResponseStatsDto> stats = statsClient.getStats(start, end, uris, unique);

        assertNotNull(stats);
        assertEquals(1, stats.size());
        assertEquals("/api/test", stats.get(0).getUri());
        assertEquals(5L, stats.get(0).getHits());
    }
}

