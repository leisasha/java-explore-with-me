package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.EventRating;

import java.util.Optional;

public interface EventRatingRepository extends JpaRepository<EventRating, Long> {
    Optional<EventRating> findByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT SUM(er.rating) FROM EventRating er WHERE er.event.id = :eventId")
    Integer getEventTotalRating(Long eventId);
}
