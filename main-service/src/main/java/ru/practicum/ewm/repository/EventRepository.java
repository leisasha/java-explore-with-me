package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    Set<Event> findByIdIn(List<Long> eventIds);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (COALESCE(:categories) IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND ((COALESCE(:rangeStart) IS NULL AND COALESCE(:rangeEnd) IS NULL AND e.eventDate >= CURRENT_TIMESTAMP) " +
            "OR (e.eventDate BETWEEN :rangeStart AND :rangeEnd)) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR " +
            "(SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = e.id AND pr.status = 'CONFIRMED') < e.participantLimit)")
    Page<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' AND e.id = :id")
    Optional<Event> findPublicEventsById(Long id);

    @Query("SELECT e FROM Event e WHERE " +
            "(COALESCE(:users) IS NULL OR e.initiator.id IN :users) " +
            "AND (COALESCE(:states) IS NULL OR e.state IN :states) " +
            "AND (COALESCE(:categories) IS NULL OR e.category.id IN :categories) " +
            "AND ((COALESCE(:rangeStart) IS NULL AND COALESCE(:rangeEnd) IS NULL AND e.eventDate >= CURRENT_TIMESTAMP) " +
            "OR (e.eventDate BETWEEN :rangeStart AND :rangeEnd))")
    Page<Event> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
