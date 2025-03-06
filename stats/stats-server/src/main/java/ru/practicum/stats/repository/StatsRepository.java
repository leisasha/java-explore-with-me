package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = """
            SELECT
                app,
                uri,
                COUNT(*) as hits
            FROM endpoint_hits
            WHERE timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR uri = ANY(string_to_array(:uris, ',')))
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<Object[]> getStats(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            @Param("uris") String uris);

    @Query(value = """
            SELECT
                app,
                uri,
                COUNT(DISTINCT ip) as hits
            FROM endpoint_hits
            WHERE timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR uri = ANY(string_to_array(:uris, ',')))
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<Object[]> getUniqueStats(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("uris") String uris);


}

