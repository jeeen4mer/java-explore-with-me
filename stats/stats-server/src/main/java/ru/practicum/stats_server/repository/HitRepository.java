package ru.practicum.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("""
            select new ru.practicum.dto.ResponseStatsDto(h.app, h.uri,
                case when :unique = true then count(distinct h.ip)
                     else count(h.ip) end as hits)
            from Hit h
            where h.timestamp between :start and :end
              and (:uris is null or h.uri in :uris)
            group by h.app, h.uri
            order by hits desc
            """)
    List<ResponseStatsDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            @Param("unique") boolean unique);

}
