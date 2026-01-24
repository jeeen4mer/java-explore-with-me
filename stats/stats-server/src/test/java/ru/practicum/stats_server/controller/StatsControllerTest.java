package ru.practicum.stats_server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.config.DateTimeConfig;
import ru.practicum.stats_server.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
@Import(DateTimeConfig.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HitService hitService;

    @Test
    void saveHit_withCustomDateTimeFormat_shouldReturnCreated() throws Exception {

        String json = "{" + """
                  "app": "app1",
                  "uri": "/api/test",
                  "ip": "127.0.0.1",
                  "timestamp": "2025-12-28 14:57:38"
                }
                """;

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(hitService, times(1)).save(any());
    }

    @Test
    void findStats_withValidParams_shouldReturnList() throws Exception {

        ResponseStatsDto statsDto =
                new ResponseStatsDto("app1", "/api/test", 5L);

        when(hitService.find(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(List.of("/api/test")),
                eq(true)
        )).thenReturn(List.of(statsDto));

        mockMvc.perform(get("/stats")
                        .param("start", "2025-12-27 00:00:00")
                        .param("end", "2025-12-28 23:59:59")
                        .param("uris", "/api/test")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app1"))
                .andExpect(jsonPath("$[0].uri").value("/api/test"))
                .andExpect(jsonPath("$[0].hits").value(5));

        verify(hitService, times(1))
                .find(any(), any(), eq(List.of("/api/test")), eq(true));
    }

    @Test
    void findStats_startAfterEnd_shouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/stats")
                        .param("start", "2025-12-29 00:00:00")
                        .param("end", "2025-12-28 00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Дата начала диапазона не может быть позже даты конца"));
    }
}
