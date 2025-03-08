package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.EndpointHitDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = new EndpointHit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        );
        statsRepository.save(hit);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        String urisString = (uris == null || uris.isEmpty()) ? null : String.join(",", uris);

        List<Object[]> rawStats = unique ?
                statsRepository.getUniqueStats(startTime, endTime, urisString) :
                statsRepository.getStats(startTime, endTime, urisString);

        return rawStats.stream()
                .map(obj -> new ViewStatsDto((String) obj[0], (String) obj[1], ((Number) obj[2]).longValue()))
                .collect(Collectors.toList());
    }
}
