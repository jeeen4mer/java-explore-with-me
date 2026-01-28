package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    public void saveHit(String appName, String uri, String ip, LocalDateTime timestamp) {
        HitDto hitDto = new HitDto(appName, uri, ip, timestamp);
        restTemplate.postForEntity("/hit", hitDto, void.class);
    }

    public List<ResponseStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String uri = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParamIfPresent("uris", Optional.ofNullable(uris))
                .queryParamIfPresent("unique", Optional.ofNullable(unique))
                .toUriString();

        ParameterizedTypeReference<List<ResponseStatsDto>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<ResponseStatsDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                responseType
        );

        return response.getBody();
    }
}
