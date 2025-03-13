package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.participationRequest.ParticipationRequest;
import ru.practicum.ewm.model.participationRequest.RequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long userId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByIdIn(List<Long> ids);

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestStatus status);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventId(Long eventId);

    @Query("""
            SELECT
                COUNT(r)
            FROM ParticipationRequest r
            WHERE r.event.id = :eventId AND r.status = :status
            """)
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("SELECT pr.event.id, COUNT(pr) FROM ParticipationRequest pr " +
            "WHERE pr.event.id IN :eventIds AND pr.status = 'CONFIRMED' " +
            "GROUP BY pr.event.id")
    List<Object[]> countConfirmedRequestsForEvents(List<Long> eventIds);
}
