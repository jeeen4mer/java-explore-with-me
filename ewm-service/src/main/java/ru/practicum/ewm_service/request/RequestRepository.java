package ru.practicum.ewm_service.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.request.model.ParticipationRequest;
import ru.practicum.ewm_service.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByRequesterIdAndEventId(long requesterId, long eventId);

    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    @Modifying
    @Query(
            """
                    update ParticipationRequest r
                    set r.status = :newStatus
                    where r.event.id = :eventId
                    and r.status = :oldStatus
                    """)
    void cancelAllRequests(long eventId, RequestStatus newStatus, RequestStatus oldStatus);

    int countByEventIdAndStatus(long eventId, RequestStatus status);

    @Query(
            """
                    select r.event.id as eventId, count(r.id) as count
                    from ParticipationRequest r
                    where r.event.id in :eventsIds
                    and r.status = :status
                    group by r.event.id
                    """
    )
    List<CountRequests> findIdsAndCountConfirmedRequestsByEventIds(List<Long> eventsIds, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(long eventId);

}
