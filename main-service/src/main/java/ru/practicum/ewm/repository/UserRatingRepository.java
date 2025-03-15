package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserRatingRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT
                u.id,
                u.name,
                SUM(CASE WHEN er.liked = TRUE THEN 1 ELSE -1 END)
            FROM EventRating er
            JOIN er.event e
            JOIN e.initiator u
            GROUP BY u.id, u.name
            ORDER BY 3 DESC
            """)
    List<Object[]> findUserRatings();
}
