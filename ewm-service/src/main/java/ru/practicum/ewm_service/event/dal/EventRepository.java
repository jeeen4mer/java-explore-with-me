package ru.practicum.ewm_service.event.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndInitiatorId(long id, long initiatorId);

    Page<Event> findByInitiatorIdAndIdGreaterThanEqualOrderById(long initiatorId, long eventId, Pageable pageable);

    @Query("""
            select e
            from Event e
            where (:users is null or e.initiator.id in :users)
            and (:states is null or e.state in :states)
            and (:categories is null or e.category.id in :categories)
            and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart)
            and (cast(:rangeEnd as timestamp) is null or e.eventDate < :rangeEnd)
            """)
    Page<Event> findAllByAdminFilters(List<Long> users,
                                      List<EventState> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Pageable pageable);

    @Query("""
                select e
                from Event e
                where (:text is null or lower(e.annotation) like :text or lower(e.description) like :text)
                  and (:categories is null or e.category.id in :categories)
                  and (:paid is null or e.paid = :paid)
                  and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart)
                  and (cast(:rangeEnd as timestamp) is null or e.eventDate < :rangeEnd)
                  and e.state = :state
            """)
    Page<Event> findAllByPublicFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("state") EventState state,
            Pageable pageable
    );

    @Query("""
                select e
                from Event e
                where (:text is null or lower(e.annotation) like :text or lower(e.description) like :text)
                  and (:categories is null or e.category.id in :categories)
                  and (:paid is null or e.paid = :paid)
                  and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart)
                  and (cast(:rangeEnd as timestamp) is null or e.eventDate < :rangeEnd)
                  and e.state = :state
                  and e.initiator.id in :ids
            """)
    Page<Event> findAllByInitiatorIdAndFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("state") EventState state,
            @Param("ids") List<Long> ids,
            Pageable pageable
    );


}
