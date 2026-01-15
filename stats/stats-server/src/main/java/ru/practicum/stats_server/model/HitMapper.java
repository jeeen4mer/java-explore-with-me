package ru.practicum.stats_server.model;

import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;

@Component
public class HitMapper {
    public Hit fromDto(HitDto dto) {
        Hit hit = new Hit();
        hit.setIp(dto.getIp());
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setTimestamp(dto.getTimestamp());
        return hit;
    }


}
