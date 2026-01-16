package ru.practicum.stats_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.service.HitService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HitService hitService;

    @Autowired
    private ObjectMapper mapper;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now();
    }

    @TestConfiguration
    static class CustomObjectMapperConfig {

        @Bean
        public ObjectMapper customObjectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper;
        }
    }

    @Test
    void testSaveHit_shouldReturnCreated() throws Exception {
        HitDto hitDto = new HitDto("app1", "/api/test", "127.0.0.1",
                LocalDateTime.of(2025, 12, 28, 14, 57, 38));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated());

        verify(hitService, times(1)).save(any(HitDto.class));
    }

    @Test
    void testFindStats_shouldReturnList() throws Exception {
        ResponseStatsDto statsDto = new ResponseStatsDto("app1", "/api/test", 5L);

        when(hitService.find(any(LocalDateTime.class), any(LocalDateTime.class),
                eq(List.of("/api/test")), eq(true)))
                .thenReturn(List.of(statsDto));

        mockMvc.perform(get("/stats")
                        .param("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("uris", "/api/test")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app1"))
                .andExpect(jsonPath("$[0].uri").value("/api/test"))
                .andExpect(jsonPath("$[0].hits").value(5));

        verify(hitService, times(1))
                .find(any(LocalDateTime.class), any(LocalDateTime.class), eq(List.of("/api/test")), eq(true));
    }

    @Test
    void testFindStats_startAfterEnd_shouldReturnBadRequest() throws Exception {
        LocalDateTime invalidStart = end.plusDays(1);

        mockMvc.perform(get("/stats")
                        .param("start", invalidStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .param("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Дата начала диапазона не может быть позже даты конца"));
    }
}
