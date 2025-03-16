package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.eventRating.EventRating;
import ru.practicum.ewm.model.eventRating.EventRatingId;

import java.util.List;
import java.util.Optional;

public interface EventRatingRepository extends JpaRepository<EventRating, EventRatingId> {
    Optional<EventRating> findById(EventRatingId id);

    Long countByEventAndLiked(Event event, Boolean liked);

    @Query("""
            SELECT
                e,
                (COUNT(CASE WHEN er.liked = true THEN 1 ELSE 0 END)
                - COUNT(CASE WHEN er.liked = false THEN 1 ELSE 0 END)) AS rating
            FROM Event e
            LEFT JOIN EventRating er ON er.event = e
            GROUP BY e.id
            ORDER BY rating DESC
            """)
    List<Object[]> findEventRatings();

    @Modifying
    @Query("DELETE FROM EventRating er WHERE er.user.id = :userId AND er.event.id = :eventId")
    void deleteByUserAndEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

}
