package ru.practicum.stats_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.exception.ValidationException;
import ru.practicum.stats_server.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final HitService service;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void saveHit(@RequestBody @Valid HitDto hitDto) {
        service.save(hitDto);
    }

    @GetMapping("/stats")
    public List<ResponseStatsDto> findStats(@RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(required = false, defaultValue = "false") boolean unique) {
        if (start.isAfter(end))
            throw new ValidationException("Дата начала диапазона не может быть позже даты конца");

        return service.find(start, end, uris, unique);
    }
}
