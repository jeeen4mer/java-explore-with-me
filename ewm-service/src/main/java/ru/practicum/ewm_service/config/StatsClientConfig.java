package ru.practicum.ewm_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.client.StatsClient;

@Configuration
public class StatsClientConfig {

    @Value("${stats-server.url}")
    private String statsServerUri;

    @Bean
    public StatsClient statsClient(RestTemplate restTemplate) {
        return new StatsClient(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(statsServerUri)
                .build();
    }
}
